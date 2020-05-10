package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.TaskCommand
import com.nanabell.sponge.nico.module.quest.data.task.KillTask
import com.nanabell.sponge.nico.module.quest.data.task.LevelGainTask
import com.nanabell.sponge.nico.module.quest.data.task.LinkDiscordTask
import com.nanabell.sponge.nico.module.quest.data.task.MineBlockTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*
import kotlin.reflect.KClass

@Permissions
@RegisterCommand(["new"], TaskCommand::class)
class TaskNewCommand : StandardCommand<QuestModule>() {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.choices("type".toText(), mapOf<String, KClass<out ITask>>(
                        "block" to MineBlockTask::class,
                        "level" to LevelGainTask::class,
                        "kill" to KillTask::class,
                        "link" to LinkDiscordTask::class
                )),
                GenericArguments.optional(
                        GenericArguments.remainingRawJoinedStrings("name".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val uniqueId = UUID.randomUUID()
        val name = args.getOne<String>("name").orNull() // TODO: Add names to Tasks

        val task: ITask = when (args.requireOne<KClass<out ITask>>("type")) {
            MineBlockTask::class -> MineBlockTask(uniqueId, 0, null)
            LevelGainTask::class -> LevelGainTask(uniqueId, 0)
            KillTask::class -> KillTask(uniqueId, 0, null)
            LinkDiscordTask::class -> LinkDiscordTask(uniqueId)
            else -> throw IllegalArgumentException("Invalid Argument type!")
        }

        taskRegistry.set(task)
        source.sendMessage(task.getName()
                .concat(NicoConstants.SPACE)
                .concat(task.getMessage()
                        .action(TextActions.showText("Click here to edit...".gray()
                                .concat(Text.NEW_LINE).concat(task.id.toString().darkGray())))
                        .action(TextActions.runCommand("/task edit ${task.id}"))))

        return CommandResult.success()
    }

    override fun getDescription(): String = "Create a new Unattached Task"

}