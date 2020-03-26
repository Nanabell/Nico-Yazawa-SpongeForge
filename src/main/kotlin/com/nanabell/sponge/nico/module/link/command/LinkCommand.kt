package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.NicoConstants
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
@RegisterCommand(["link"], hasExecutor = false)
class LinkCommand : StandardCommand<LinkModule>() {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.discordUser("discord".toText()),
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.user("minecraft".toText()),
                                permissions.getOthers()
                        )
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val discord = args.requireOne<DiscordUser>("discord")
        val minecraft = source.requireUserOrArg(args, "minecraft")

        if (linkService.isLinked(minecraft)) {
            source.sendMessage("${minecraft.name} is already linked!".red())
            return CommandResult.empty()
        }

        if (linkService.isLinked(discord)) {
            source.sendMessage("${discord.asTag} is already linked!".red())
            return CommandResult.empty()
        }

        val result = linkService.link(discord, minecraft)
        if (result == LinkResult.LINKED) {
            source.sendMessage("${minecraft.name} is now linked to ${discord.asTag}".green())
            return CommandResult.success()
        }

        source.sendMessage("Failed to link ${minecraft.name} to ${discord.asTag}! Error: $result".red())
        return CommandResult.empty()
    }

    override fun getDescription(): String = "Command related to Minecraft <--> Discord Link"

}
