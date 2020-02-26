package com.nanabell.sponge.nico.link.discord

import com.nanabell.sponge.nico.NicoYazawa
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.hooks.ListenerAdapter

class DiscordService : ListenerAdapter() {

    private val logger = NicoYazawa.getPlugin().getLogger(javaClass.simpleName)
    private val config = NicoYazawa.getPlugin().getConfig()

    val jda: JDA
    val guild: Guild
    val self: Member

    init {
        val config = config.get().discordLinkConfig
        jda = JDABuilder(config.token).build().awaitReady()

        guild = jda.getGuildById(config.guildId) ?: throw IllegalStateException("Guild ${config.guildId} not found!")
        this.self = guild.selfMember
    }

    fun init() {
        jda.addEventListener(UsernameRequestListener())
        setInitialReaction()
    }

    fun getUser(userId: Long) = jda.getUserById(userId)
    fun getUser(tag: String) = jda.getUserByTag(tag)

    fun getMember(id: Long) = guild.getMemberById(id)
    fun getMember(tag: String) = guild.getMemberByTag(tag)

    fun getRole(idLong: Long) = guild.getRoleById(idLong)
    fun getRole(id: String) = guild.getRoleById(id)

    fun addRole(member: Member, role: Role): Boolean {
        if (!self.canInteract(role)) {
            logger.warn("Attempted to add role ${role.name} to member ${member.user.asTag} but bot cannot interact with requested role.")
            return false
        }

        return try {
            guild.controller.addSingleRoleToMember(member, role).complete().also {
                logger.info("Added Role {} to Member {}", role.name, member.user.asTag)
            }.run { true }
        } catch (e: RuntimeException) {
            logger.debug("Role assignment failed ${role.name} to member ${member.user.asTag}", e).run { false }
        }
    }

    fun removeRole(member: Member, role: Role): Boolean {
        if (!self.canInteract(role)) {
            logger.warn("Attempted to remove role {} to member {} but bot cannot interact with requested role.", role.name, member.user.asTag)
            return false
        }

        return try {
            guild.controller.removeSingleRoleFromMember(member, role).also {
                logger.info("Removed Role {} from Member {}", role.name, member.user.asTag)
            }. run { true }
        } catch (e: RuntimeException) {
            logger.debug("Role removal failed! Role: ${role.name}, Member: ${member.user.asTag}", e).run { false }
        }
    }

    private fun setInitialReaction() {
        val config = config.get().discordLinkConfig

        val channel = guild.getTextChannelById(config.channelId)
        if (channel != null) {
            val message = channel.getHistoryAround(config.messageId, 1).complete().getMessageById(config.messageId)
            if (message != null && message.reactions.isEmpty()) {
                message.addReaction(config.reactionEmote).queue {
                    logger.info("Added initial reaction ${config.reactionEmote} to message ${message.id} in channel ${channel.name}")
                }
            }
        }
    }
}
