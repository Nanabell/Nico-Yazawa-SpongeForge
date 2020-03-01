package com.nanabell.sponge.nico.module.activity.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.NoPlayerArgCommandException
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.red
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

@Permissions
@RegisterCommand(["reset"], CooldownCommand::class)
class CooldownResetCommand : StandardCommand<ActivityModule>() {

    private val service: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(GenericArguments.playerOrSource("player".toText()))
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        if (!args.hasAny("player")) throw NoPlayerArgCommandException()

        val players = args.getAll<Player>("player")
        if (players.size == 1) {
            val player = players.first()

            source.sendMessage(if (service.removeCooldown(player))
                "Reset cooldown for ${players.first().name}".toText().green()
            else
                "Player is not on cooldown".toText().red())

            return CommandResult.success()
        }

        var counter = 0
        players.forEach { if (service.removeCooldown(it)) counter++ }

        source.sendMessage(if (counter == 0)
            "Reset cooldowns for $counter players.".toText().green()
        else
            "None of the selected players were on cooldown.".toText().red())

        return CommandResult.successCount(counter)
    }


    override fun getDescription(): String = "Reset the cooldown of (a) player(s)"

}
