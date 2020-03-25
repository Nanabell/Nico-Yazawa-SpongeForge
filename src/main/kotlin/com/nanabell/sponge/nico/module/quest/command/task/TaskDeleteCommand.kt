package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.TaskCommand
import com.nanabell.sponge.nico.module.quest.data.task.InvalidTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions
@RegisterCommand(["delete"], TaskCommand::class)
class TaskDeleteCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-force", "f")
                        .buildWith(NicoConstants.task("task".toText()))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val task = args.requireOne<ITask>("task")
        if (task is InvalidTask) {
            source.sendMessage("There is no Task with the id ${task.id}".red())
            return CommandResult.empty()
        }

        if (!args.hasAny("force")) {
            source.sendMessage(confirmMessage(task))
            return CommandResult.success()
        }

        taskRegistry.remove(task)
        questRegistry.getAll().forEach { quest ->
            if (quest.rewards.contains(task.id)) {
                quest.rewards.remove(task.id)
                questRegistry.set(quest)
            }
        }

        source.sendMessage("[".green()
                .concat(task.getText())
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .action(TextActions.showText(task.id.toString().darkGray()))
                .concat(" has been permanently deleted!".green()))

        return CommandResult.success()
    }

    private fun confirmMessage(task: ITask): Text {
        return "Are you sure you want to delete the task: ".green()
                .concat(Text.NEW_LINE)
                .concat("[".green())
                .concat(task.getText().action(TextActions.showText(task.id.toString().darkGray())))
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .concat(Text.NEW_LINE)
                .concat("This action cannot be undone".red())
                .concat(Text.NEW_LINE)
                .concat("The task will be removed from any attached Quests!".red())
                .concat("[Confirm]".darkRed()
                        .action(TextActions.showText("Click here to delete the quest...".gray()))
                        .action(TextActions.runCommand("/${this::class.getCommandString()} ${task.id} -f")))
    }

    override fun getDescription(): String = "Delete a Task"
}