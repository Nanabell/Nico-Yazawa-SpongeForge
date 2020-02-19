package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.color
import com.nanabell.sponge.nico.extensions.orNull
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
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class LinkCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

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
                                GenericArguments.longNum(Text.of("target")), "nico.link.others")))
                .child(acceptCommand.spec(), *acceptCommand.aliases())
                .child(denyCommand.spec(), *denyCommand.aliases())
                .build()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) throw CommandException(Text.of("Command can only be run by a Player"))
        val discordId = args.getOne<Long>("target").orNull()
        if (discordId != null) {
            TODO("Implement Manual Linking")
        } else {

            val link = linkService.getLink(src)
            if (link == null) {
                src.sendMessage("Your Account is not linked to a Discord user!".red())
                return CommandResult.success()
            }

            val discordUser = link.fetchUser(Sponge.getServiceManager().provideUnchecked(DiscordService::class.java).jda)
            val minecraftUser = link.fetchUser(Sponge.getServiceManager().provideUnchecked(UserStorageService::class.java))
            val msg = Text.builder("Account Linking Status:")
                    .color(TextColors.GOLD)
                    .append(Text.NEW_LINE)
                    .append("Discord: ".toText())
                    .append((discordUser?.asTag ?: "[Unable to Fetch Discord Username (${link.discordId})]").toText()
                            .color(if (discordUser != null) TextColors.GOLD else TextColors.RED))
                    .append("Minecraft: ".toText())
                    .append((minecraftUser?.name ?: "[Failed to Fetch Minecraft Name (${link.minecraftId})]").toText()
                            .color(if (minecraftUser != null) TextColors.GOLD else TextColors.RED))
                    .build()
            
            src.sendMessage(msg)
        }

        return CommandResult.success()
    }
}