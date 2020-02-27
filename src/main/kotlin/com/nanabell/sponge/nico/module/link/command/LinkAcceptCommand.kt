package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.extension.darkRed
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.misc.LinkResult
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text

@RegisterCommand(["accept"], LinkCommand::class)
class LinkAcceptCommand : AbstractCommand<Player, LinkModule>() {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun executeCommand(source: Player, args: CommandContext, cause: Cause): CommandResult {
        val message: Text = when (linkService.confirmLink(source)) {
            LinkResult.LINKED -> "Successfully Linked Discord Account".toText().green()
            LinkResult.NO_LINK_REQUEST -> "There are no pending Link Requests!".toText().red()
            LinkResult.ALREADY_LINKED -> "Your Account is already Linked".toText().red()
            else -> "Unknown Failure! Contact Administrator to take a look at the Logs!".toText().darkRed()
        }

        source.sendMessage(message)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Accept a pending Discord Link Request"
}