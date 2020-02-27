package com.nanabell.sponge.nico.internal

import com.nanabell.sponge.nico.internal.extension.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandPermissionException
import org.spongepowered.api.command.args.ArgumentParseException
import org.spongepowered.api.event.Listener
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.reflect.KClass

// Generic
open class MissingAnnotationException : Exception {
    constructor(clazz: KClass<*>, annotation: KClass<*>): super("'$clazz' is missing the Annotation @${annotation.simpleName}")
    constructor(message: String): super(message)
}

// Service
class InvalidSubClassException(clazz: KClass<*>, parent: KClass<*>) : Exception("Service '$clazz' is not a SubClass of '$parent'")

// Event
class MissingEventListenersException(clazz: KClass<*>) : MissingAnnotationException("Event Listener '$clazz' does not contain any @${Listener::class.simpleName}")

// Command
class IllegalCommandException(message: String) : Exception(message)

class NicoArgumentParseException(
        message: Text,
        source: String,
        position: Int,
        private val usage: Text?,
        private val subcommands: Text?,
        val isEnd: Boolean
) : ArgumentParseException(message, source, position) {

    override fun getText(): Text? {
        val t = super.getText()
        return if (this.usage == null && subcommands == null) {
            t
        } else Text.join(t, Text.NEW_LINE, getUsage())
    }

    private fun getUsage(): Text? {
        val builder = Text.builder()
        if (usage != null) {
            builder.append(Text.NEW_LINE).append("Usage: ".toText()).append(usage)
        }
        if (subcommands != null) {
            builder.append(Text.NEW_LINE).append("SubCommands: ".toText()).append(subcommands)
        }

        return builder.build()
    }
}

class NicoCommandException(private val exceptions: List<Pair<String, CommandException>>) : CommandException(Text.EMPTY) {

    override fun getText(): Text? {
        if (exceptions.isEmpty()) { // Unable to get the error.
            return "Command Failed with unknown Reason".toText()
        }

        // Is it only command permission exceptions?
        if (exceptions.stream().allMatch { pair: Pair<String, CommandException> -> CommandPermissionException::class.java.isInstance(pair.second) }) {
            return exceptions[0].second.text
        }

        if (exceptions.stream().allMatch { pair: Pair<String, CommandException> -> pair.second is NicoArgumentParseException && (pair.second as NicoArgumentParseException).isEnd }) {
            return if (exceptions.size == 1) {
                return Text.of("ArgumentParseException: ${exceptions[0].first}".toText(), Text.NEW_LINE, TextColors.RED, exceptions[0].second.text)
            } else {
                print(exceptions)
            }
        }

        val lce: List<Pair<String, CommandException>> = exceptions.stream()
                .filter { pair: Pair<String, CommandException> -> pair.second !is NicoArgumentParseException || !(pair.second as NicoArgumentParseException).isEnd }
                .filter { pair: Pair<String, CommandException> -> !CommandPermissionException::class.java.isInstance(pair.second) }
                .collect(Collectors.toList())

        if (lce.size == 1) {
            return Text.of("Command Exception: ${exceptions[0].first}".toText(), Text.NEW_LINE, TextColors.RED, exceptions[0].second.text)
        }

        return print(lce)
    }

    private fun print(exceptions: List<Pair<String, CommandException>>): Text {
        val sept: Text = ", ".toText()
        val builder: Text.Builder = "Multiple Exceptions: ".toText().toBuilder()

        exceptions.forEach(Consumer { x: Pair<String, CommandException> ->
            builder.append(Text.NEW_LINE).append(sept)
                    .append(Text.NEW_LINE)
                    .append("Command Exception: ${x.first}".toText())
                    .append(Text.NEW_LINE)
                    .append(x.second.text)
        })

        builder.append(Text.NEW_LINE).append(sept)
        return builder.toText()
    }
}