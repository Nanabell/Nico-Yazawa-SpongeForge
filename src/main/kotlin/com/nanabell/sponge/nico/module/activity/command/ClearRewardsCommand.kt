package com.nanabell.sponge.nico.module.activity.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause

@RegisterCommand(["clear"], RewardCommand::class)
class ClearRewardsCommand : StandardCommand<ActivityModule>() {

    private val activity: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-all", "a")
                        .buildWith(GenericArguments.playerOrSource("player".toText()))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        if (args.hasAny("all")) {
            activity.clearPayouts()

            source.sendMessage("Reward tracking has been cleared".toText().green())
            return CommandResult.success()
        }

        var successCount = 0
        val players = args.getAll<Player>("player")
        players.forEach {
            if (activity.clearPayouts(it))
                successCount++

        }

        source.sendMessage("Reward tracking for ${players.size} have been cleared".toText().green())
        return CommandResult.successCount(successCount)
    }

    override fun getDescription(): String = "" // TODO: Add Description

}
