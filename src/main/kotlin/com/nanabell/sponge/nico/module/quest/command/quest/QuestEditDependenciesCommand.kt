package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
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
@RegisterCommand(["dependency"], QuestEditCommand::class)
class QuestEditDependenciesCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.quest("quest".toText()),
                GenericArguments.optional(
                        NicoConstants.quest("dependency".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<IQuest>("quest")
        val dependency = args.getOne<IQuest>("dependency").orNull()

        if (dependency == null) {
            source.sendMessage(printQuests(quest))
            return CommandResult.success()
        }

        if (quest.dependencies.contains(dependency.id)) {
            quest.dependencies.remove(dependency.id)
        } else {
            quest.dependencies.add(dependency.id)
        }

        questRegistry.set(quest)
        return Sponge.getCommandManager().process(source, "quest edit ${quest.id}")
    }

    private fun printQuests(quest: IQuest): Text {
        var message = "Available Quests:".green().concat(Text.NEW_LINE)
        questRegistry.getAll().filter { it.id != quest.id && !quest.dependencies.contains(it.id) }.forEach {
            message = message.concat(it.id.toString().yellow()
                    .action(TextActions.showText("Click here to set as Dependency...".gray()
                            .concat(Text.NEW_LINE)
                            .concat(it.getText())))
                    .action(TextActions.runCommand("/quest edit dependency ${quest.id} ${it.id}")))
        }

        return message
    }

    override fun getDescription(): String = "Edit a Quests Dependencies"
}