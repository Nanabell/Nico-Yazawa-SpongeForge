package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.misc.LinkResult
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause

@Permissions(supportsOthers = true)
@RegisterCommand(["remove"], LinkCommand::class)
class UnlinkCommand : StandardCommand<LinkModule>() {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.optional(
                        GenericArguments.onlyOne(
                                GenericArguments.requiringPermission(
                                        GenericArguments.player("player".toText()),
                                        permissions.getOthers()
                                )
                        )
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val target = source.requirePlayerOrArg(args, "player")

        val message = when (linkService.unlink(target)) {
            LinkResult.UNLINKED -> "Successfully removed ${target.name}'s Discord Link".toText().green()
            LinkResult.NOT_LINKED -> "This Account is not Linked!".toText().red()
            else -> "Unknown failure! Contact Administrators!".toText().darkRed()
        }

        source.sendMessage(message)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Unlink an already Linked account"

}