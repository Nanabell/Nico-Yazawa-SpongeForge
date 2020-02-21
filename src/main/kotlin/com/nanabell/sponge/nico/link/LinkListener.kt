package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.*
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import net.dv8tion.jda.api.entities.MessageChannel
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors

class LinkListener {

    private val logger = NicoYazawa.getLogger()
    private val config = NicoYazawa.getConfig()

    private val discordService = Sponge.getServiceManager().provideUnchecked(DiscordService::class.java)
    private val linkService: LinkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)
    private val permService = Sponge.getServiceManager().provide(PermissionService::class.java).orNull()

    private val eventManager: EventManager = Sponge.getEventManager()

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        val context = event.cause.context
        val user = context.get(LinkEventContextKeys.DISCORD_USER).orNull()
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

            context.get(LinkEventContextKeys.MESSAGE_CHANNEL).ifPresent { messageChannel: MessageChannel ->
                messageChannel.sendMessage(event.target + " is not online.\nProvide the name of an online player.").queue()
            }
            return
        }

        if (linkService.isLinked(player)) {
            context.get(LinkEventContextKeys.MESSAGE_CHANNEL).ifPresent {
                it.sendMessage("${event.target} is already Linked.\nProvide the name of an online player.").queue()
            }
            return
        }

        discordService.removePending(user.idLong)
        linkService.addPending(user, player)

        val msg: Text = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.asTag + " "))
                .append(Text.of(TextColors.GREEN,
                        TextActions.runCommand("/nico link accept"),
                        TextActions.showText("/nico link accept".toText()),
                        "[Confirm]"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED,
                        TextActions.runCommand("/nico link deny"),
                        TextActions.showText("/nico link deny".toText()),
                        "[Deny]"))
                .build()
        player.sendMessage(msg)
    }

    @Listener
    fun onLinkStageChange(event: LinkStateChangeEvent) {
        if (event.state == LinkState.LINKED) {
            val discordConfig = config.get().discordLinkConfig

            val mUser = event.context[LinkEventContextKeys.MINECRAFT_USER].orNull()
            if (mUser == null) {
                logger.error("Missing LinkEventContextKeys#MINECRAFT_USER on LinkStateChangeEvent with LinkState#LINKED")
                return
            }

            val player = mUser.player.orNull()

            // Attempt Minecraft Role Sync
            if (discordConfig.linkGroup != null) {
                if (permService != null) {
                    if (permService.groupSubjects.allIdentifiers.get().contains(discordConfig.linkGroup)) {
                        val targetReference = permService.groupSubjects.newSubjectReference("default")

                        if (mUser.subjectData.allParents[emptySet()]?.contains(targetReference) == true) {
                            logger.warn("Unable to apply linking-group. User already has $targetReference")
                        } else {
                            mUser.subjectData.addParent(emptySet(), targetReference).whenComplete { result, throwable ->

                                if (result == true) {
                                    player?.sendMessage("You've been given the Group ".toText().gold().concat(targetReference.subjectIdentifier.toText().lightPurple()))
                                    logger.info("Applied linking-group ${targetReference.subjectIdentifier} to User ${mUser.name}")

                                } else {
                                    player?.sendMessage("Unable to assign Group ${targetReference.subjectIdentifier}! Contact an Administrator".toText().darkRed())
                                    logger.error("Unable to assign Group ${targetReference.subjectIdentifier} to User ${mUser.name}", throwable)
                                }
                            }
                        }
                    } else {
                        logger.warn("Unable to apply linking-group role. Missing group ${discordConfig.linkGroup}")
                    }
                } else {
                    logger.warn("Unable to apply linking-group. Missing PermissionService")
                }
            }

            // Attempt Discord role Sync
            if (discordConfig.linkRole != -1L) {
                val dUser = event.context[LinkEventContextKeys.DISCORD_USER].orNull()
                if (dUser == null) {
                    logger.error("Missing LinkEventContextKeys#DISCORD_USER on LinkStateChangeEvent with LinkState#LINKED")
                    return
                }

                val guild = discordService.jda.getGuildById(discordConfig.guildId)
                if (guild != null) {

                    val role = guild.roleCache.parallelStreamUnordered().filter { role -> role.idLong == discordConfig.linkRole }.findFirst().orNull()
                    if (role != null) {

                        val member = guild.getMember(dUser)
                        if (member != null) {

                            if (member.roles.contains(role)) {
                                logger.warn("Unable to apply linking-role. User ${dUser.asTag} already has Discord Role ${role.name}")
                            } else {

                                guild.controller.addRolesToMember(member, role).queue({
                                    player?.sendMessage("You've been given the Discord Role ".toText().gold()
                                            .concat(role.name.toText().lightPurple())
                                            .concat(" in the Guild ".toText().gold())
                                            .concat(guild.name.toText().lightPurple()))

                                    logger.info("Applied Discord role ${role.name} to discord user ${dUser.asTag} in ${guild.name}")
                                }, {
                                    player?.sendMessage("Unable to apply Discord Role ${role.name} in Guild ${guild.name}, Contact an Administrator!".toText().darkRed())
                                    logger.error("Unable to apply Discord Role ${role.name} in Guild ${guild.name} to user ${dUser.asTag}")
                                })
                            }
                        }
                    } else {
                        logger.warn("Unable to find specified Role in Discord Config")
                    }

                } else {
                    logger.warn("Unable to find specified guild in DiscordConfig")
                }
            }
        }
    }
}