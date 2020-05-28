package com.nanabell.sponge.nico.module.sync.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.core.command.NicoCommand
import com.nanabell.sponge.nico.module.sync.SyncModule
import com.nanabell.sponge.nico.module.sync.misc.TroopSource
import com.nanabell.sponge.nico.module.sync.service.TroopSyncService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["sync"], NicoCommand::class, true)
class SyncCommand : StandardCommand<SyncModule>() {

    private val service: TroopSyncService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(GenericArguments.playerOrSource("player".toText()))
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val config = getModule().getConfigOrDefault()
        val player = args.requireOne<Player>("player")

        if (config.discordSync)
            service.sync(player, TroopSource.DISCORD)

        if (config.minecraftSync)
            service.sync(player, TroopSource.MINECRAFT)

        return CommandResult.success()
    }

    override fun getDescription(): String = "Forcefully Sync Troops"
}