package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.extensions.darkRed
import com.nanabell.sponge.nico.extensions.gold
import com.nanabell.sponge.nico.extensions.red
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.module.link.misc.LinkResult
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text

class LinkAcceptCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    override fun aliases(): Array<String> {
        return arrayOf("accept")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Accept a pending Discord-Link Request"))
                .permission("nico.command.accept.base")
                .arguments(Args.optional(Args.requiringPermission(Args.playerOrSource(Text.of("target")), "nico.command.accept")))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.accept.base").register()
        builder.id("nico.command.accept").register()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = src.requirePlayerOrArg(args, "target")

        val message: Text = when (linkService.confirmLink(target)) {
            LinkResult.LINKED -> "Successfully Linked Discord Account".toText().gold()
            LinkResult.NO_LINK_REQUEST -> "There are no pending Link Requests!".toText().red()
            LinkResult.ALREADY_LINKED -> "Your Account is already Linked".toText().red()
            else -> "Unknown Failure! Contact Administrator to take a look at the Logs!".toText().darkRed()
        }

        src.sendMessage(message)
        return CommandResult.success()
    }
}