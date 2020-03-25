package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["reload"])
class QuestReloadCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        questRegistry.reload()

        source.sendMessage("All Quests have been reloaded!".green())
        return CommandResult.success()
    }

    override fun getDescription(): String = "Reload all Quests from the QuestStore"
}