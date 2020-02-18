package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.SelfSpecCommand
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class LinkCommand : CommandExecutor, SelfSpecCommand {

    override fun aliases(): Array<String> {
        return arrayOf("link")
    }

    override fun spec(): CommandSpec {
        val acceptCommand = LinkAcceptCommand()
        val denyCommand = LinkDenyCommand()
        return CommandSpec.builder()
                .description(Text.of("Commands to View / Accept / Deny pending & existing Discord-Links"))
                .executor(this)
                .arguments(GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.longNum(Text.of("target")), "nico.link.others"
                        )))
                .child(acceptCommand.spec(), *acceptCommand.aliases())
                .child(denyCommand.spec(), *denyCommand.aliases())
                .build()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) throw CommandException(Text.of("Command can only be run by a Player"))
        val oDiscordId = args.getOne<Long>("target")
        if (oDiscordId.isPresent) {
        }
        return CommandResult.success()
    }
}