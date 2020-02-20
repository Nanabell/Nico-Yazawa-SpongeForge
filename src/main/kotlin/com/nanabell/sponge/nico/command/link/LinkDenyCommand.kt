package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.extensions.gold
import com.nanabell.sponge.nico.extensions.red
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.LinkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text

class LinkDenyCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)


    override fun aliases(): Array<String> {
        return arrayOf("deny", "d")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Deny a pending Discord-Link request"))
                .permission("nico.command.link.deny.self")
                .arguments(GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.player(Text.of("target")), "nico.command.link.deny.others")))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder): List<PermissionDescription> {
        return listOf(builder.id("nico.command.link.deny.self").register(),
                builder.id("nico.command.link.deny.others").register())
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (!args.hasAny(Text.of("target")) && src !is Player) {
            throw CommandException(Text.of("Cannot Target " + src.name + ". Valid Target is [Player]"))
        }

        val target = if (src is Player) src else args.requireOne("target")

        if (linkService.removePending(target))
            src.sendMessage("Removed Pending Link Request".toText().gold())
        else
            src.sendMessage("There are no pending Link Requests!".toText().red())

        return CommandResult.success()
    }
}