package com.nanabell.sponge.nico.discord

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import com.nanabell.sponge.nico.link.LinkService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.PrivateChannel
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
    private val linkService: LinkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    private var jda: JDA? = null
    private val pendingUsername: MutableSet<Long> = HashSet()


    override fun onGuildMessageReactionAdd(@Nonnull event: GuildMessageReactionAddEvent) {
        val user = event.user
        if (user.isBot || user.isFake) return
        val config = configManager.get().discordLinkConfig
        if (event.messageIdLong == config.messageId) {
            if (event.reactionEmote.asCodepoints == config.reactionEmote) {
                if (!linkService.pendingLink(user)) {
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
        }
        pendingUsername.remove(user.idLong)
        val username = event.message.contentRaw.split(" ").toTypedArray()[0]
        val eventContext = EventContext.builder()
                .add(LinkEventContextKeys.MESSAGE_CHANNEL, event.channel)
                .add(LinkEventContextKeys.USER, event.author)
                .build()
        Sponge.getEventManager().post(LinkRequestEvent(username, Cause.of(eventContext, this)))
    }

    init {
        try {
            this.jda = JDABuilder(configManager.get().discordLinkConfig.token).build().awaitReady()
        } catch (e: InterruptedException) {
            logger.error("Error connecting to discord", e)
        } catch (e: LoginException) {
            logger.error("Failed to Log into Discord! Please Provide a valid Token!", e)
        }

        val jda = this.jda
        if (jda != null) {
            jda.addEventListener(this)
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
}