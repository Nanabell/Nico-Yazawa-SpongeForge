package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.RewardCommand
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["reload"], RewardCommand::class)
class RewardReloadCommand : StandardCommand<QuestModule>() {

    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        rewardRegistry.reload()

        source.sendMessage("All Rewards have been reloaded!".green())
        return CommandResult.success()
    }

    override fun getDescription(): String = "Reload all Rewards from the RewardStore"
}