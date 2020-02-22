package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.*
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.LinkState
import net.dv8tion.jda.api.entities.User
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class LinkCommand : CommandExecutor, SelfSpecCommand {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    private val acceptCommand = LinkAcceptCommand()
    private val denyCommand = LinkDenyCommand()
    private val unlinkCommand = UnlinkCommand()

    override fun aliases(): Array<String> {
        return arrayOf("link")
    }

    override fun spec(): CommandSpec {

        return CommandSpec.builder()
                .description(Text.of("Commands to View / Accept / Deny pending & existing Discord-Links"))
                .permission("nico.command.link.view")
                .executor(this)
                .arguments(Args.optional(Args.seq(
                        Args.playerOrSource("player".toText()),
                        Args.requiringPermission(NicoConstants.discordUser("target".toText()), "nico.command.link.discord")
                )))
                .child(acceptCommand.spec(), *acceptCommand.aliases())
                .child(denyCommand.spec(), *denyCommand.aliases())
                .child(unlinkCommand.spec(), *unlinkCommand.aliases())
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.link.view").register()
        builder.id("nico.command.link.discord").register()
        builder.id("nico.command.link.others").register()

        acceptCommand.permissionDescriptions(builder)
        denyCommand.permissionDescriptions(builder)
        unlinkCommand.permissionDescriptions(builder)
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val user = args.getOne<User>("target").orNull()
        if (user != null) {
            val target = src.requirePlayerOrArg(args, "player")

            val result = linkService.link(user, target)
            val message = when (result.state) {
                LinkState.LINKED -> "Successfully Linked ${target.name} to ${user.asTag}".toText().gold()
                LinkState.ALREADY_LINKED -> "This Account is already Linked!".toText().red()
                else -> "Unknown failure! Contact Administrators! $result".toText().darkRed()
            }

            src.sendMessage(message)
            return CommandResult.success()
        }

        val link = linkService.getLink(src.requirePlayerOrArg(args, "player"))
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
                .append(Text.NEW_LINE)
                .append("Minecraft: ".toText())
                .append((minecraftUser?.name ?: "[Failed to Fetch Minecraft Name (${link.minecraftId})]").toText()
                        .color(if (minecraftUser != null) TextColors.GOLD else TextColors.RED))
                .build()

        src.sendMessage(msg)

        return CommandResult.success()
    }
}
