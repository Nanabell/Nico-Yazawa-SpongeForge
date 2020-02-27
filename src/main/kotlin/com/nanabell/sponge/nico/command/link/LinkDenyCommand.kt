package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.internal.extension.gold
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
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

class LinkDenyCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)


    override fun aliases(): Array<String> {
        return arrayOf("deny")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Deny a pending Discord-Link request"))
                .permission("nico.command.deny.base")
                .arguments(Args.optional(Args.requiringPermission(Args.player(Text.of("target")), "nico.command.deny")))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.deny.base").register()
        builder.id("nico.command.deny").register()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = src.requirePlayerOrArg(args, "target")

        if (linkService.removePending(target))
            src.sendMessage("Removed Pending Link Request".toText().gold())
        else
            src.sendMessage("There are no pending Link Requests!".toText().red())

        return CommandResult.success()
    }
}