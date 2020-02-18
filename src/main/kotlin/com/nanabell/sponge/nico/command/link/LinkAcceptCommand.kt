package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.LinkResult
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
import org.spongepowered.api.text.Text

class LinkAcceptCommand : CommandExecutor, SelfSpecCommand {

    private val logger = NicoYazawa.getLogger()
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
        if (!linkService.pendingLink(target)) {
            if (linkService.isLinked(target)) {
                logger.warn("Requested to Accept Link while account is already linked.")
                // Already Linked
            } else {
                logger.warn("Requested to Accept Link while account is not pending to be linked")
                // No Pending Link
            }
            return CommandResult.success()
        }
        val result = linkService.confirmLink(target)
        if (result !== LinkResult.SUCCESS) {
            logger.warn("Failed to Link Account {}. Result: {}", target, result)
        }

        src.sendMessage("Successfully Linked Discord Account".toText())
        return CommandResult.success()
    }
}