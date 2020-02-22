package com.nanabell.sponge.nico.discord

import com.nanabell.sponge.nico.NicoYazawa
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.hooks.ListenerAdapter

class DiscordService : ListenerAdapter() {

    private val configManager = NicoYazawa.getConfig()

    val jda: JDA = JDABuilder(configManager.get().discordLinkConfig.token).build().awaitReady()

    fun init() {
        jda.addEventListener(UsernameRequestListener())
        setInitialReaction()
    }

    fun getUserById(userId: Long): User? = jda.getUserById(userId)
    fun getUserById(userId: String): User? = jda.getUserById(userId)

    private fun setInitialReaction() {
        val config = configManager.get().discordLinkConfig
        val guild = jda.getGuildById(config.guildId)
        if (guild != null) {
            val channel = guild.getTextChannelById(config.channelId)
            if (channel != null) {
                val message = channel.getHistoryAround(config.messageId, 1).complete().getMessageById(config.messageId)
                if (message != null && message.reactions.isEmpty()) {
                    message.addReaction(config.reactionEmote).queue()
                }
            }
        }
    }
}