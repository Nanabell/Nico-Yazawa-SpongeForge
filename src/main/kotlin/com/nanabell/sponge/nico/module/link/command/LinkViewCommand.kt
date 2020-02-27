package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text

@Permissions(supportsOthers = true)
@RegisterCommand(["info"], LinkCommand::class)
class LinkViewCommand : StandardCommand<LinkModule>() {

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

        val link = linkService.getLink(target)
        if (link == null) {
            source.sendMessage("${target.name} does not have a Link entry!".toText())
            return CommandResult.success()
        }

        val msg = "Account Link for ".toText().green().toBuilder()
                .append(target.name.toText().yellow())
                .append(Text.NEW_LINE).append(Text.NEW_LINE)
                .append("Discord:".toText().yellow()).append(Text.NEW_LINE)
                .append((link.fetchDiscordUser()?.asTag ?: "[${link.discordId}]").toText().white())
                .append(Text.NEW_LINE).append(Text.NEW_LINE)
                .append("Minecraft:".toText().yellow()).append(Text.NEW_LINE)
                .append((link.fetchMinecraftUser()?.name ?: "[${link.minecraftId}]").toText().white())
                .build()

        source.sendMessage(msg)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Info about link status of a User"
}
