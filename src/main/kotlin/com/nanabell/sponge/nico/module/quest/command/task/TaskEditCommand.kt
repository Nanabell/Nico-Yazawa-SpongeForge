package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.TaskCommand
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions
@RegisterCommand(["edit"], TaskCommand::class)
class TaskEditCommand : StandardCommand<QuestModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)

    override fun getArguments(): Array<CommandElement> = arrayOf(NicoConstants.task("task".toText(), true))

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val task = args.requireOne<ITask>("task")
        val messages = mutableListOf<Text>()

        messages.add("ID: ".green().concat(task.id.toString().yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        messages.add("Type: ".green().concat(task.type.yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        pagination.builder().contents(messages.plus(task.printSettings()))
                .title("Editing Task ".green().concat(task.type.yellow()))
                .sendTo(source)

        return CommandResult.success()
    }

    override fun getDescription(): String = "View the Current Task Settings"
}