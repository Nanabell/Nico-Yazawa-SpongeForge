package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions
@RegisterCommand(["task"], QuestEditCommand::class)
class QuestEditTasksCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.quest("quest".toText()),
                GenericArguments.optional(
                        NicoConstants.task("task".toText(), false)
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest>("quest")
        val task = args.getOne<com.nanabell.sponge.nico.module.quest.interfaces.task.ITask>("task").orNull()

        if (task == null) {
            source.sendMessage(printUnattachedTasks(quest))
            return CommandResult.success()
        }

        if (quest.tasks.contains(task.id)) {
            quest.tasks.remove(task.id)
        } else {
            quest.tasks.add(task.id)
        }

        questRegistry.set(quest)
        return Sponge.getCommandManager().process(source, "quest edit ${quest.id}")
    }

    private fun printUnattachedTasks(quest: com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest): Text {
        var message = "Unattached Tasks:".green().concat(Text.NEW_LINE)
        taskRegistry.getAll().filter { !it.isAttached() }.forEach {
            message = message.concat(it.id.toString().yellow()
                    .action(TextActions.showText("Click here to Attach to Quest...".gray()
                            .concat(Text.NEW_LINE)
                            .concat(it.getText().darkGray())))
                    .action(TextActions.runCommand("/quest edit task ${quest.id} ${it.id}")))
        }

        return message
    }

    override fun getDescription(): String = "Add a Task to a Quest"
}