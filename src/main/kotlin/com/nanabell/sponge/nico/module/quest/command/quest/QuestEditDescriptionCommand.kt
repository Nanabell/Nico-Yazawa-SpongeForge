package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.toText
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

@Permissions
@RegisterCommand(["description"], QuestEditCommand::class)
class QuestEditDescriptionCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.quest("quest".toText()),
                GenericArguments.optional(
                        GenericArguments.remainingRawJoinedStrings("description".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<IQuest>("quest")
        val description = args.getOne<String>("description")

        quest.description = description.orNull()
        questRegistry.set(quest)

        return Sponge.getCommandManager().process(source, "quest edit ${quest.id}")
    }

    override fun getDescription(): String = "Edit a Quests Description"
}