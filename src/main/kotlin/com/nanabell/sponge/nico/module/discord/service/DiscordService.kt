package com.nanabell.sponge.nico.module.discord.service

import com.nanabell.sponge.nico.internal.annotation.service.ApiService
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.discord.DiscordModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView

@ApiService
@RegisterService
class DiscordService : AbstractService<DiscordModule>() {

    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var self: Member

    override fun onPreEnable() {
        val config = module.getConfigOrDefault()
        jda = JDABuilder(config.token).setAudioEnabled(false).setEventManager(AnnotatedEventManager()).build()
        guild = jda.getGuildById(config.guildId) ?: throw IllegalStateException("Unable to find guild ${config.guildId}. Check Configs!")
        self = guild.selfMember
    }

    fun getUser(userId: Long) = jda.getUserById(userId)
    fun getUser(tag: String) = jda.getUserByTag(tag)

    fun getMember(id: Long) = guild.getMemberById(id)

    fun getRole(idLong: Long) = guild.getRoleById(idLong)

    fun getTextChannel(idLong: Long) = guild.getTextChannelById(idLong)

    fun addRole(member: Member, role: Role): Boolean {
        if (checkInteract(role))
            return false

        return try {
            guild.controller.addSingleRoleToMember(member, role).complete().also {
                logger.info("Added Role {} to Member {}", role.name, member.user.asTag)
            }.run { true }
        } catch (e: RuntimeException) {
            logger.debug("Role assignment failed ${role.name} to member ${member.user.asTag}", e).run { false }
        }
    }

    fun removeRole(member: Member, role: Role): Boolean {
        if (!checkInteract(role))
            return false

        return try {
            guild.controller.removeSingleRoleFromMember(member, role).also {
                logger.info("Removed Role {} from Member {}", role.name, member.user.asTag)
            }.run { true }
        } catch (e: RuntimeException) {
            logger.debug("Role removal failed! Role: ${role.name}, Member: ${member.user.asTag}", e).run { false }
        }
    }

    fun registerListener(listener: Any) {
        jda.addEventListener(listener)
    }

    fun getUserCache(): SnowflakeCacheView<User> = jda.userCache

    private fun checkInteract(role: Role): Boolean {
        if (!self.canInteract(role)) {
            logger.warn("Attempted interact with Role '{}' but cannot interact with role as '{}'", role.name, self.effectiveName)
            return false
        }

        return true
    }

/*    private fun setInitialReaction() {
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
    }*/

}
