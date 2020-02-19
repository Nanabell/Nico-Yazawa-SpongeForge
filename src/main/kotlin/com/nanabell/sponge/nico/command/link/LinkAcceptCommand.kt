package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.LinkState
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.text.Text

class LinkAcceptCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    override fun aliases(): Array<String> {
        return arrayOf("accept", "a")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Accept a pending Discord-Link Request"))
                .arguments(GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.player(Text.of("target")), "nico.link.accept.others")))
                .executor(this)
                .build()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (!args.hasAny(Text.of("target")) && src !is Player) {
            throw CommandException(Text.of("Cannot Target " + src.name + ". Valid Target is [Player]"))
        }

        val target = if (src is Player) src else args.requireOne("target")

        val result = linkService.confirmLink(target, Cause.of(EventContext.empty(), this))
        val message: Text = when (result.state) {
            LinkState.LINKED -> "Successfully Linked Discord Account".toText()
            LinkState.ALREADY_LINKED -> "User is already linked to a different Discord Account".toText()
            LinkState.NO_LINK_REQUEST -> "There are no pending Link Requests!".toText()
            else -> "Unknown Failure! Contact Administrator to take a look at the Logs!".toText()
        }

        src.sendMessage(message)
        return CommandResult.success()
    }
}