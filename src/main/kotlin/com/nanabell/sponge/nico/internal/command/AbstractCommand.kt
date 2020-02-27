package com.nanabell.sponge.nico.internal.command

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.internal.IllegalCommandException
import com.nanabell.sponge.nico.internal.MissingAnnotationException
import com.nanabell.sponge.nico.internal.NicoArgumentParseException
import com.nanabell.sponge.nico.internal.NicoCommandException
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.annotation.command.RunAsync
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.*
import org.spongepowered.api.command.args.ArgumentParseException
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.parsing.InputTokenizer
import org.spongepowered.api.command.args.parsing.SingleArg
import org.spongepowered.api.command.dispatcher.SimpleDispatcher
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.util.TextMessageException
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@Suppress("UNCHECKED_CAST")
abstract class AbstractCommand<T : CommandSource, M : ConfigurableModule<*, *>> : CommandCallable {

    private val sourceType: Class<T>
    private val sourceTypePredicate: (CommandSource) -> Boolean

    protected val logger: Logger
    private val plugin: NicoYazawa
    private val hasExecutor: Boolean
    private val isRoot: Boolean
    private val isAsync: Boolean = this::class.findAnnotation<RunAsync>() != null

    private val commandPath: String
    private val permissions: CommandPermissionHandler

    val aliases: Array<String>

    private val usageCommand by lazy { UsageCommand(this) }
    private val dispatcher: SimpleDispatcher = SimpleDispatcher(SimpleDispatcher.FIRST_DISAMBIGUATOR)

    private lateinit var moduleCommands: Set<KClass<out AbstractCommand<*, *>>>
    private lateinit var commandBuilder: CommandBuilder
    private lateinit var module: StandardModule

    private lateinit var argumentParser: CommandElement

    init {
        val co = javaClass.getAnnotation(RegisterCommand::class.java) ?: throw MissingAnnotationException(this::class, RegisterCommand::class)
        if (co.value.isEmpty()) throw IllegalCommandException("value Element of @${RegisterCommand::class} at $javaClass cannot be empty")

        val types = this::class.getActualTypeArguments(AbstractCommand::class)
        this.sourceType = if (types.isEmpty()) CommandSource::class.java as Class<T> else types[0] as Class<T>

        sourceTypePredicate = if (sourceType.isAssignableFrom(CommandSource::class.java)) {
            { true }
        } else {
            { sourceType.isInstance(it) }
        }

        this.isRoot = co.subCommandOf == AbstractCommand::class
        this.hasExecutor = co.hasExecutor

        this.commandPath = getSubCommandPath()
        this.aliases = co.value

        this.permissions = NicoYazawa.getPlugin().getPermissionRegistry().getHandler(this::class)
        this.plugin = NicoYazawa.getPlugin()
        this.logger = plugin.getLogger("Command", javaClass.simpleName)
    }

    private fun getSubCommandPath(): String {
        val builder = StringBuilder()
        getNextSubCommandPath(this::class, builder, false)
        return builder.toString()
    }

    private fun getNextSubCommandPath(clazz: KClass<out AbstractCommand<*, *>>, builder: StringBuilder, appendPeriod: Boolean) {
        val co = clazz.findAnnotation<RegisterCommand>() ?: throw MissingAnnotationException(clazz, RegisterCommand::class)
        if (!co.subCommandOf.isAbstract && co.subCommandOf.java != this::class)
            getNextSubCommandPath(co.subCommandOf, builder, true)

        builder.append(co.value[0])
        if (appendPeriod) builder.append('.')
    }

    fun postInit() {
        this.argumentParser = Args.seq(*getArguments())
        createChildCommands()

        afterPostInit()

        getAdditionalPermissions().forEach { permissions.registerPermission(it) }
        getAdditionalSuffixPermission().forEach { permissions.registerSuffixPermission(it) }
    }

    protected open fun getArguments(): Array<CommandElement> {
        return emptyArray()
    }

    protected open fun getAdditionalPermissions(): List<String> {
        return emptyList()
    }

    protected open fun getAdditionalSuffixPermission(): List<String> {
        return emptyList()
    }

    protected open fun afterPostInit() {}

    final override fun process(source: CommandSource, arguments: String): CommandResult {
        val args = CommandArgs(arguments, tokenizer.tokenize(arguments, false))

        return process(source, commandPath.replace('.', ' '), arguments, args)
    }

    @Throws(CommandException::class)
    private fun process(source: CommandSource, command: String, arguments: String, args: CommandArgs): CommandResult {

        val context = CommandContext()
        val throws = mutableListOf<Pair<String, CommandException>>()

        val castedSource: T

        try {
            if (args.hasNext() && this.dispatcher.containsAlias(args.peek())) {
                val snapshot = args.snapshot
                val next = args.next()

                try {
                    val callable = this.dispatcher.get(next.toLowerCase()).orNull()?.callable
                    if (callable is AbstractCommand<*, *>)
                        return callable.process(source, "$command $next", arguments, args)

                    if (callable != null)
                        return callable.process(source, arguments)
                } catch (e: CommandException) {
                    throws.add("$command $next" to e)
                } finally {
                    args.applySnapshot(snapshot)
                }
            }

            castedSource = checkSourceType(source)
            if (!testPermissionOnSubject(castedSource)) {
                throw CommandPermissionException()
            }

            if (!this.hasExecutor) {
                if (throws.isEmpty()) {
                    return this.usageCommand.processInternal(source, args.nextIfPresent().map { it.toLowerCase() }.orNull())
                }

                throw NicoCommandException(throws)
            }

            this.argumentParser.parse(source, args, context)
            if (args.hasNext()) {
                throws.add(command to NicoArgumentParseException("Too many arguments".toText().red(), args.raw, args.rawPosition, Text.of(getSimpleUsage(source)), getChildrenUsage(source), true))
                throw NicoCommandException(throws)
            }

        } catch (e: NicoCommandException) {
            throw e
        } catch (ape: ArgumentParseException) {
            throws.add(command to NicoArgumentParseException("${ape.message?.toText()}".toText().red(), "", ape.position, getSimpleUsage(source).toText(), getChildrenUsage(source), ape is NicoArgumentParseException && ape.isEnd)).also {
                throw NicoCommandException(throws)
            }
        } catch (e: CommandException) {
            throws.add(command to e).also { throw NicoCommandException(throws) }
        } catch (t: Throwable) {
            val message = if (t.message == null) "null" else t.message

            throws.add(command to CommandException("Unexpected Command failure! $message".toText()))
            logger.error("Unexpected Command failure in command $command!", t)

            throw NicoCommandException(throws)
        }

        val cause = Sponge.getCauseStackManager().currentCause
        if (this.isAsync) {
            Sponge.getScheduler().createAsyncExecutor(plugin).execute { onExecute(castedSource, context, cause) }
        }

        return onExecute(castedSource, context, cause)
    }

    private fun onExecute(source: T, context: CommandContext, cause: Cause): CommandResult {
        try {
            return executeCommand(source, context, cause)
        } catch (e: TextMessageException) {
            source.sendMessage("Unexpected Command Failure! ${e.text}".toText())
        } catch (t: Throwable) {
            val message = if (t.message != null) t.message else "null"
            logger.error("Command has thrown an unexpected exception!", t)

            source.sendMessage("Unexpected Command Failure! $message".toText().darkRed())
        }

        return CommandResult.empty()
    }

    @Throws(Exception::class)
    abstract fun executeCommand(source: T, args: CommandContext, cause: Cause): CommandResult

    private fun checkSourceType(source: CommandSource): T {
        if (this.sourceTypePredicate.invoke(source))
            return source as T

        throw when {
            sourceType == Player::class.java && source !is Player -> CommandException("Command can only be executed by a Player!".toText())
            sourceType == Player::class.java && source !is Player -> CommandException("Command can only be executed by the Console".toText())
            sourceType == Player::class.java && source !is Player -> CommandException("Command can only be executed by a CommandBlock".toText())
            else -> CommandException("Unknown or Unsupported SourceType!".toText())
        }
    }


    @Throws(CommandException::class)
    final override fun getSuggestions(source: CommandSource, arguments: String, targetPosition: Location<World>?): List<String> {
        val singleArgs: MutableList<SingleArg> = Lists.newArrayList<SingleArg>(tokenizer.tokenize(arguments, false))

        // If we end with a space - then we add another argument.
        if (arguments.isEmpty() || arguments.endsWith(" ")) {
            singleArgs.add(SingleArg("", arguments.length - 1, arguments.length - 1))
        }

        val args = CommandArgs(arguments, singleArgs)
        val context = CommandContext()

        val options: MutableList<String> = Lists.newArrayList()
        context.putArg(CommandContext.TAB_COMPLETION, true) // We don't care for the value.

        // Subcommand
        val state = args.snapshot
        options.addAll(dispatcher.getSuggestions(source, arguments, targetPosition))
        args.applySnapshot(state)

        options.addAll(argumentParser.complete(source, args, context))
        return options.stream().distinct().collect(Collectors.toList())
    }

    final override fun testPermission(source: CommandSource): Boolean {
        return testPermissionOnSubject(source)
    }

    private fun testPermissionOnSubject(source: Subject): Boolean {
        return this.permissions.checkBase(source)
    }

    final override fun getShortDescription(source: CommandSource): Optional<Text> {
        return getDescription().toText().toOptional()
    }

    final override fun getHelp(source: CommandSource): Optional<Text> {
        val extended = getExtendedDescription()?.toText() ?: return getDescription().toText().toOptional()

        return getDescription().toText().concat(Text.NEW_LINE).concat(NicoConstants.SPACE).concat(Text.NEW_LINE).concat(extended).toOptional()
    }

    abstract fun getDescription(): String

    protected open fun getExtendedDescription(): String? = null

    final override fun getUsage(source: CommandSource): Text {
        return getUsageString(source).toText()
    }

    private fun getUsageString(source: CommandSource): String {
        val builder: java.lang.StringBuilder = java.lang.StringBuilder("/")
                .append(commandPath.replace("\\.".toRegex(), " "))
                .append(" ")

        dispatcher.primaryAliases.stream().map { x: String -> dispatcher[x, source].orElse(null) }
                .filter { x: CommandMapping? -> x != null && x.callable.testPermission(source) }
                .forEach { x: CommandMapping -> builder.append(x.primaryAlias).append("|") }
        return builder.append(argumentParser.getUsage(source).toPlain().replace("\\?\\|".toRegex(), "")).toString()
    }

    private fun getSimpleUsage(source: CommandSource): String {
        return "/" + commandPath.replace("\\.".toRegex(), " ") + " " + argumentParser.getUsage(source).toPlain()
    }

    fun getChildrenUsage(source: CommandSource): Text? {
        val primary: MutableSet<String> = Sets.newHashSet(dispatcher.primaryAliases)
        primary.removeIf { x: String -> x.equals("?", ignoreCase = true) || x.equals("help", ignoreCase = true) }
        if (primary.isEmpty()) {
            return null
        }
        val s: List<Text> = primary.stream()
                .filter { x: String -> dispatcher[x].get().callable.testPermission(source) }
                .map { x: String ->

                    val toSuggest = "/" + commandPath.replace("\\.".toRegex(), " ") + " " + x
                    Text.builder(x)
                            .onClick(TextActions.suggestCommand(toSuggest))
                            .onHover(TextActions.showText("Click to enter the Command: $toSuggest".toText()))
                            .onShiftClick(TextActions.insertText("$toSuggest ?"))
                            .color(TextColors.AQUA)
                            .build()
                }.collect(Collectors.toList())
        return if (s.isEmpty()) null else Text.joinWith(Text.of(", "), s)
    }

    private fun createChildCommands() {
        val children = this.moduleCommands.stream().filter {
            it.findAnnotation<RegisterCommand>().let { rc -> rc != null && rc.subCommandOf == this::class }
        }

        children.forEach {
            try {
                this.commandBuilder.buildCommand(it, false).also { sub -> this.dispatcher.register(sub, *sub.aliases) }
            } catch (e: Exception) {
                logger.error("Failed to construct Child Command for Class $it", e)
            }
        }

        this.dispatcher.register(usageCommand, "?", "help")
    }

    /**
     * Set the reference to the rest of the command in this module.
     *
     * Command Implementations or their Consumers should not try to set this value
     * This method can only be called once! Any subsequent calls will be ignored
     *
     * @param commandSet The Set of AbstractCommand Classes
     */
    fun setModuleCommands(commandSet: Set<KClass<out AbstractCommand<*, *>>>) {
        if (!::moduleCommands.isInitialized)
            this.moduleCommands = commandSet
    }

    /**
     * Set the [CommandBuilder] used by the Module. Necessary for Root Command to construct their children.
     *
     * Command Implementations or their Consumers should not try to set this value
     * This method can only be called once! Any subsequent calls will be ignored
     *
     * @param builder The CommandBuilder for the current Module
     */
    fun setCommandBuilder(builder: CommandBuilder) {
        if (!::commandBuilder.isInitialized)
            this.commandBuilder = builder
    }

    /**
     * Set the reference to the constructing parent Module
     *
     * Command Implementations or their Consumers should not try to set this value
     * This method can only be called once! Any subsequent calls will be ignored
     *
     * @param module The parent Module
     */
    fun setModule(module: StandardModule) {
        if (!this::module.isInitialized)
            this.module = module
    }

    /**
     * Get the Module for the current Command. This should never be null can can be used safely.
     *
     * @return The Module casted to the Module Command Type Parameter
     */
    fun getModule(): M {
        return module as M
    }

    private class UsageCommand(private val parentCommand: AbstractCommand<*, *>) : CommandCallable, CommandExecutor {
        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            return process(src, "")
        }

        override fun process(source: CommandSource, arguments: String): CommandResult {
            return processInternal(source, null)
        }

        fun processInternal(source: CommandSource, previous: String?): CommandResult {
            if (!testPermission(source)) {
                source.sendMessage("You do not have permission to view this help page.".toText().red())
                return CommandResult.empty()
            }

            return try {
                val textMessages = usage(source, previous)

                // Header
                val command: String = parentCommand.commandPath.replace("\\.".toRegex(), " ")
                val header: Text = "Usage for ".toText().green().concat("/$command".toText().yellow())
                val ps = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)
                val builder = ps.builder().title(header).contents(textMessages)
                if (source !is Player) {
                    builder.linesPerPage(-1)
                }

                builder.sendTo(source)
                CommandResult.success()
            } catch (e: CommandPermissionException) {
                source.sendMessage("You do not have permission to view this help page.".toText().red())
                CommandResult.empty()
            }
        }

        @Throws(CommandPermissionException::class)
        fun usage(source: CommandSource, previous: String?): List<Text> {
            if (!testPermission(source)) {
                throw CommandPermissionException()
            }

            val textMessages: MutableList<Text> = mutableListOf()
            if (previous != null) {
                textMessages.add("The command ".toText().red().concat(previous.toText().yellow()).concat(" is not a valid subcommand.".toText().red()))
                textMessages.add(NicoConstants.SPACE)
            }

            if (parentCommand.sourceType == Player::class.java) {
                textMessages.add("The command can only be run by players.".toText().yellow())
            }

            textMessages.add("Module: ".toText().yellow()
                    .concat(parentCommand.module.moduleName.toText().white())
                    .concat(" (ID: ".toText().yellow())
                    .concat(parentCommand.module.moduleId.toText().white())
                    .concat(")".toText().yellow()))

            val desc: String = parentCommand.getDescription()
            if (desc.isNotEmpty()) {
                textMessages.add(NicoConstants.SPACE)
                textMessages.add("Summary: ".toText().yellow())
                textMessages.add(Text.of(desc))
            }

            val ext: String = parentCommand.getExtendedDescription() ?: ""
            if (ext.isNotEmpty()) {
                textMessages.add(NicoConstants.SPACE)
                textMessages.add("Description: ".toText().yellow())
                val split = ext.split("(\\r|\\n|\\r\\n)").toTypedArray()
                for (s in split) {
                    textMessages.add(Text.of(s))
                }
            }

            if (parentCommand.hasExecutor) {
                textMessages.add(NicoConstants.SPACE)
                textMessages.add("Usage: ".toText().yellow())
                textMessages.add(Text.of(TextColors.WHITE, parentCommand.getSimpleUsage(source)))
            }

            parentCommand.getChildrenUsage(source)?.apply {
                textMessages.add(NicoConstants.SPACE)
                textMessages.add("Available subcommands:".toText().yellow())
                textMessages.add(Text.of(TextColors.WHITE, this))
            }

            return textMessages
        }

        override fun getSuggestions(source: CommandSource, arguments: String, targetPosition: Location<World>?): List<String> {
            return Lists.newArrayList()
        }

        override fun testPermission(source: CommandSource): Boolean {
            return parentCommand.permissions.checkBase(source)
        }

        override fun getShortDescription(source: CommandSource): Optional<Text> {
            return Optional.empty()
        }

        override fun getHelp(source: CommandSource): Optional<Text> {
            return Optional.empty()
        }

        override fun getUsage(source: CommandSource): Text {
            return Text.EMPTY
        }
    }

    companion object {
        private val tokenizer: InputTokenizer = InputTokenizer.quotedStrings(false)
    }
}

abstract class StandardCommand<M : ConfigurableModule<*, *>> : AbstractCommand<CommandSource, M>()