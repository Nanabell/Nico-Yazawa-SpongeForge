package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.extensions.darkRed
import com.nanabell.sponge.nico.extensions.gold
import com.nanabell.sponge.nico.extensions.red
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.LinkState
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionDescription

class UnlinkCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    override fun aliases(): Array<String> = arrayOf("unlink")

    override fun spec(): CommandSpec = CommandSpec.builder()
            .description("Unlink a already Linked Discord Account".toText())
            .permission("nico.command.unlink.base")
            .arguments(Args.optional(Args.requiringPermission(Args.playerOrSource("player".toText()), "nico.command.unlink")))
            .executor(this)
            .build()

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.unlink.base").register()
        builder.id("nico.command.unlink").register()
    }

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = src.requirePlayerOrArg(args, "player")

        val result = linkService.unlink(target)

        val message = when (result.state) {
            LinkState.UNLINKED -> "Successfully unlinked ${target.name}'s Discord Link".toText().gold()
            LinkState.NOT_LINKED -> "This Account is not Linked!".toText().red()
            else -> "Unknown failure! Contact Administrators!".toText().darkRed()
        }

        src.sendMessage(message)
        return CommandResult.success()
    }

}