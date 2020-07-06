package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.PlaytimeTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import java.time.Duration

@Permissions
@RegisterCommand(["duration"], TaskEditCommand::class)
class TaskEditDurationCommand : StandardCommand<QuestModule>() {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.task("task".toText(), true),
                GenericArguments.optional(
                        GenericArguments.duration("duration".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val task = args.requireOne<ITask>("task")
        val duration = args.requireOne<Duration>("duration")

        when (task) {
            is PlaytimeTask -> task.duration = duration
            else -> throw CommandException("Task $task does not have a Duration property!".red())
        }

        taskRegistry.set(task)
        return Sponge.getCommandManager().process(source, "task edit ${task.id}")
    }

    override fun getDescription(): String = "Edit a Tasks Duration Property"
}