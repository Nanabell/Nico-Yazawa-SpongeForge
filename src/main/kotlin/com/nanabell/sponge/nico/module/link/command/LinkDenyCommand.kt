package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause

@RegisterCommand(["deny"], LinkCommand::class)
class LinkDenyCommand : AbstractCommand<Player, LinkModule>() {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun executeCommand(source: Player, args: CommandContext, cause: Cause): CommandResult {
        val message = when (linkService.removePending(source)) {
            true -> "Removed Pending Link Request".toText().green()
            false -> "There are no pending Link Requests!".toText().red()
        }

        source.sendMessage(message)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Deny a pending Discord Link request"
}