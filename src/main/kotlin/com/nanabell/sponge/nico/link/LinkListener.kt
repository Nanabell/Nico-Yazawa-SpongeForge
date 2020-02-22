package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.config.DiscordLinkConfig
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.discord.event.UserAcceptedEvent
import com.nanabell.sponge.nico.extensions.*
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.Role
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors

class LinkListener {

    private val logger = NicoYazawa.getLogger()
    private val config = NicoYazawa.getConfig()

    private val discordService = Sponge.getServiceManager().provideUnchecked(DiscordService::class.java)
    private val linkService: LinkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)
    private val eventManager: EventManager = Sponge.getEventManager()

    private val permService: PermissionService? get() = Sponge.getServiceManager().provide(PermissionService::class.java).orNull()

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        val context = event.cause.context
        val user = context.get(NicoConstants.DISCORD_USER).orNull()
        if (user == null) {
            logger.error("OnLinkRequest did not include a context User. $event")
            return
        }

        if (linkService.isPending(user)) {
            logger.warn("Received LinkRequestEvent for user who already has a pending Request $event")
            return  // Already Pending Link
        }

        val player = Sponge.getServer().getPlayer(event.target).orNull()
        if (player == null) {
            eventManager.post(LinkStateChangeEvent(LinkState.USER_NOT_FOUND, Cause.of(EventContext.empty(), this)))

            context.get(NicoConstants.DISCORD_CHANNEL).ifPresent { messageChannel: MessageChannel ->
                messageChannel.sendMessage(event.target + " is not online.\nProvide the name of an online player.").queue()
            }
            return
        }

        if (linkService.isLinked(player)) {
            context.get(NicoConstants.DISCORD_CHANNEL).ifPresent {
                it.sendMessage("${event.target} is already Linked.\nProvide the name of an online player.").queue()
            }
            return
        }

        eventManager.post(UserAcceptedEvent(user.idLong, Cause.of(EventContext.empty(), this)))
        linkService.addPending(user, player)

        val msg: Text = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.asTag + " "))
                .append(Text.of(TextColors.GREEN,
                        TextActions.runCommand("/nico accept"),
                        TextActions.showText("/nico accept".toText()),
                        "[Confirm]"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED,
                        TextActions.runCommand("/nico deny"),
                        TextActions.showText("/nico deny".toText()),
                        "[Deny]"))
                .build()
        player.sendMessage(msg)
    }

    @Listener
    fun onLinkStageChange(event: LinkStateChangeEvent) {
        if (event.state != LinkState.LINKED && event.state != LinkState.UNLINKED)
            return

        val discordConfig = config.get().discordLinkConfig
        val mUser = event.context[EventContextKeys.OWNER].orNull()
        if (mUser == null) {
            logger.error("Missing LinkEventContextKeys#MINECRAFT_USER on LinkStateChangeEvent with LinkState#LINKED")
            return
        }

        val dUser = event.context[NicoConstants.DISCORD_USER].orNull()
        if (dUser == null) {
            logger.error("Missing LinkEventContextKeys#DISCORD_USER on LinkStateChangeEvent with LinkState#LINKED")
            return
        }

        val player = mUser.player.orNull()
        if (event.state == LinkState.LINKED) {
            addGroup(discordConfig, mUser, player)
            addRole(discordConfig, dUser, player)
        } else if (event.state == LinkState.UNLINKED) {
            removeGroup(discordConfig, mUser, player)
            removeRole(discordConfig, dUser, player)
        }
    }

    private fun addGroup(discordConfig: DiscordLinkConfig, mUser: MinecraftUser, player: Player?) {
        val permissionService = checkGroupRequirements(discordConfig, mUser, permService, false) ?: return

        val targetReference = permissionService.groupSubjects.newSubjectReference(discordConfig.linkGroup)
        val result = mUser.subjectData.addParent(emptySet(), targetReference).get()

        if (result) {
            player?.sendMessage("You've been given the Group ".toText().gold().concat(targetReference.subjectIdentifier.toText().yellow()))
            logger.info("Applied linking-group ${targetReference.subjectIdentifier} to User ${mUser.name}")

        } else {
            player?.sendMessage("Unable to assign Group ${targetReference.subjectIdentifier}! Contact an Administrator".toText().darkRed())
            logger.error("Unable to assign Group ${targetReference.subjectIdentifier} to User ${mUser.name}")
        }
    }

    private fun removeGroup(discordConfig: DiscordLinkConfig, mUser: MinecraftUser, player: Player?) {
        val permissionService = checkGroupRequirements(discordConfig, mUser, permService, true) ?: return

        val targetReference = permissionService.groupSubjects.newSubjectReference(discordConfig.linkGroup)
        val result = mUser.subjectData.removeParent(emptySet(), targetReference).get()

        if (result) {
            player?.sendMessage("You've lost the Group ".toText().gold().concat(targetReference.subjectIdentifier.toText().yellow()))
            logger.info("Removed linking-group ${targetReference.subjectIdentifier} to User ${mUser.name}")

        } else {
            player?.sendMessage("Unable to remove Group ${targetReference.subjectIdentifier}! Contact an Administrator".toText().darkRed())
            logger.error("Unable to remove Group ${targetReference.subjectIdentifier} to User ${mUser.name}")
        }
    }

    private fun addRole(discordConfig: DiscordLinkConfig, dUser: DiscordUser, player: Player?) {
        val (guild, member, role) = checkRoleRequirements(discordConfig, dUser, discordService.jda, false) ?: return

        guild.controller.addSingleRoleToMember(member, role).queue({
            player?.sendMessage("You've been given the Discord Role ".toText().gold()
                    .concat(role.name.toText().yellow())
                    .concat(" in the Guild ".toText().gold())
                    .concat(guild.name.toText().yellow()))

            logger.info("Applied Discord role ${role.name} to discord user ${dUser.asTag} in ${guild.name}")
        }, {
            player?.sendMessage("Unable to apply Discord Role ${role.name} in Guild ${guild.name}, Contact an Administrator!".toText().darkRed())
            logger.error("Unable to apply Discord Role ${role.name} in Guild ${guild.name} to user ${dUser.asTag}")
        })
    }

    private fun removeRole(discordConfig: DiscordLinkConfig, dUser: DiscordUser, player: Player?) {
        val (guild, member, role) = checkRoleRequirements(discordConfig, dUser, discordService.jda, true) ?: return

        guild.controller.removeSingleRoleFromMember(member, role).queue({
            player?.sendMessage("You've lost the Discord Role ".toText().gold()
                    .concat(role.name.toText().yellow())
                    .concat(" in the Guild ".toText().gold())
                    .concat(guild.name.toText().yellow()))

            logger.info("Rmeoved Discord role ${role.name} to discord user ${dUser.asTag} in ${guild.name}")
        }, {
            player?.sendMessage("Unable to remove Discord Role ${role.name} in Guild ${guild.name}, Contact an Administrator!".toText().darkRed())
            logger.error("Unable to remove Discord Role ${role.name} in Guild ${guild.name} to user ${dUser.asTag}")
        })
    }

    private fun checkGroupRequirements(discordConfig: DiscordLinkConfig, mUser: MinecraftUser, permissionService: PermissionService?, isRemove: Boolean): PermissionService? {
        if (discordConfig.linkGroup.isEmpty()) {
            logger.debug("Unable to pass check for linking-group. No link-group specified in Configuration")
            return null
        }

        if (permissionService == null) {
            logger.debug("Unable to pass check for linking-group. Missing PermissionService")
            return null
        }

        if (!permissionService.groupSubjects.hasIdentifier(discordConfig.linkGroup)) {
            logger.warn("Unable to pass check for linking-group. Missing group ${discordConfig.linkGroup}")
            return null
        }

        if (isRemove) {
            if (!mUser.subjectData.hasParent(discordConfig.linkGroup)) {
                logger.warn("Unable to pass check for linking-group. User ${mUser.name} does not have ${discordConfig.linkGroup}")
                return null
            }
        } else {
            if (mUser.subjectData.hasParent(discordConfig.linkGroup)) {
                logger.warn("Unable to pass check for linking-group. User ${mUser.name} already has ${discordConfig.linkGroup}")
                return null
            }
        }

        return permissionService
    }

    private fun checkRoleRequirements(discordConfig: DiscordLinkConfig, dUser: DiscordUser, jda: JDA, isRemove: Boolean): Triple<Guild, Member, Role>? {
        if (discordConfig.linkRole == -1L) {
            logger.debug("Unable to pass check for Discord Role. No Role configured in config")
            return null
        }

        val guild = jda.getGuildById(discordConfig.guildId)
        if (guild == null) {
            logger.warn("Unable to pass check for Discord Role. Configured Guild ${discordConfig.guildId} not Found!")
            return null
        }

        val role = guild.roleCache.parallelStreamUnordered().filter { it.idLong == discordConfig.linkRole }.findFirst().orNull()
        if (role == null) {
            logger.warn("Unable to pass check for Discord Role. Configured Role ${discordConfig.linkRole} not Found!")
            return null
        }

        val member = guild.getMember(dUser)
        if (member == null) {
            logger.warn("Unable to pass check for Discord Role. User ${dUser.asTag} is not a Member of ${guild.name}")
            return null
        }

        if (isRemove) {
            if (!member.roles.contains(role)) {
                logger.warn("Unable to pass check for Discord Role. Member ${dUser.asTag} does not have the role ${role.name}")
                return null
            }
        } else {
            if (member.roles.contains(role)) {
                logger.warn("Unable to pass check for Discord Role. Member ${dUser.asTag} already has the role ${role.name}")
                return null
            }
        }


        return Triple(guild, member, role)
    }
}