package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.data.quest.InvalidQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@RegisterCommand(["delete"], QuestCommand::class)
class QuestDeleteCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-force", "f")
                        .buildWith(NicoConstants.quest("quest".toText()))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<IQuest>("quest")
        if (quest is InvalidQuest) {
            source.sendMessage("There is no Quest with the id ${quest.id}".red())
            return CommandResult.empty()
        }

        if (!args.hasAny("force")) {
            source.sendMessage(confirmMessage(quest))
            return CommandResult.empty()
        }

        questRegistry.remove(quest)
        quest.tasks().forEach {
            taskRegistry.remove(it)
        }

        userRegistry.getAll().forEach {
            it.reset(quest.id)
            userRegistry.set(it)
        }


        source.sendMessage("[".green()
                .concat(quest.getText())
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .action(TextActions.showText(quest.id.toString().darkGray()))
                .concat(" has been permanently deleted!".green()))

        return CommandResult.success()
    }

    private fun confirmMessage(quest: IQuest): Text {
        return "Are you sure you want to delete the quest: ".green()
                .concat(Text.NEW_LINE)
                .concat("[".green())
                .concat(quest.getText().action(TextActions.showText(quest.id.toString().darkGray())))
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .concat(Text.NEW_LINE)
                .concat("This action cannot be undone".red())
                .concat("[Confirm]".darkRed()
                        .action(TextActions.showText("Click here to delete the quest...".gray()))
                        .action(TextActions.runCommand("/${this::class.getCommandString()} ${quest.id} -f")))
    }

    override fun getDescription(): String = "Delete a Quest from the Registry!"
}