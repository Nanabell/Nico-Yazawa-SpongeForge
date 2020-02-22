package com.nanabell.sponge.nico.discord

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull
import javax.security.auth.login.LoginException

class DiscordService(plugin: NicoYazawa) : ListenerAdapter() {

    private val logger = NicoYazawa.getLogger()
    private val configManager: Config<MainConfig> = plugin.configManager
    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    private val pendingUsername: MutableSet<Long> = HashSet()

    lateinit var jda: JDA

    init {
        try {
            this.jda = JDABuilder(configManager.get().discordLinkConfig.token).build().awaitReady()
        } catch (e: InterruptedException) {
            logger.error("Error connecting to discord", e)
        } catch (e: LoginException) {
            logger.error("Failed to Log into Discord! Please Provide a valid Token!", e)
        }

        jda.addEventListener(this)
        setInitialReaction()
    }

    fun getUserById(userId: Long): User? = jda.getUserById(userId)
    fun getUserById(userId: String): User? = jda.getUserById(userId)

    fun removePending(userId: Long) {
        pendingUsername.remove(userId)
    }

    override fun onGuildMessageReactionAdd(@Nonnull event: GuildMessageReactionAddEvent) {
        val user = event.user
        if (user.isBot || user.isFake) return
        val config = configManager.get().discordLinkConfig
        if (event.messageIdLong == config.messageId) {
            if (event.reactionEmote.asCodepoints == config.reactionEmote) {
                if (!linkService.isPending(user)) {
                    pendingUsername.add(user.idLong)
                    user.openPrivateChannel().queue { privateChannel: PrivateChannel -> privateChannel.sendMessage("What is you Minecraft username?").queue() }
                }
            }
            event.reaction.removeReaction(user).queueAfter(1, TimeUnit.SECONDS)
        }
    }

    override fun onPrivateMessageReceived(@Nonnull event: PrivateMessageReceivedEvent) {
        val user = event.author
        if (user.isBot || user.isFake) return
        if (!pendingUsername.contains(user.idLong)) {
            logger.warn("Received Private Message from user without Linking Request")
            return
        }

        pendingUsername.remove(user.idLong)
        val username = event.message.contentRaw.split(" ").toTypedArray()[0]
        val eventContext = EventContext.builder()
                .add(NicoConstants.DISCORD_CHANNEL, event.channel)
                .add(NicoConstants.DISCORD_USER, event.author)
                .build()
        Sponge.getEventManager().post(LinkRequestEvent(username, Cause.of(eventContext, this)))
    }

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