package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.KillTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text

@Permissions
@RegisterCommand(["amount"], TaskEditCommand::class)
class TaskEditAmountCommand : StandardCommand<QuestModule>() {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.task("task".toText(), true),
                GenericArguments.optional(
                        GenericArguments.integer("amount".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val task = args.requireOne<ITask>("task")
        val amount = args.getOne<Int>("amount").orNull()

        if (amount == null) {
            source.sendMessage("You need to specific an amount argument!".red()
                    .concat(Text.NEW_LINE)
                    .concat(getSimpleUsage(source).toText()))

            return CommandResult.empty()
        }

        when (task) {
            is KillTask -> task.amount = amount
            else -> throw IllegalArgumentException("Task $task does not have a Amount property!")
        }

        taskRegistry.set(task)
        return Sponge.getCommandManager().process(source, "task edit ${task.id}")
    }

    override fun getDescription(): String = "Edit a Tasks Amount Property"
    override fun getExtendedDescription(): String? = "Will error if the Task type does not have a amount argument type!"
}