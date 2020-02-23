package com.nanabell.sponge.nico.link.discord

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.discord.event.UserAcceptedEvent
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

class UsernameRequestListener : ListenerAdapter() {

    private val logger = NicoYazawa.getPlugin().getLogger("UsernameRequestListener")
    private val config = NicoYazawa.getPlugin().getConfig()
    private val pending: MutableSet<Long> = HashSet()
    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    init {
        Sponge.getEventManager().registerListeners(NicoYazawa.getPlugin(), this)
    }

    override fun onGuildMessageReactionAdd(@Nonnull event: GuildMessageReactionAddEvent) {
        val user = event.user
        if (user.isBot || user.isFake) return

        val config = config.get().discordLinkConfig
        if (event.messageIdLong == config.messageId) {
            if (event.reactionEmote.asCodepoints == config.reactionEmote) {

                if (!linkService.isPending(user)) {
                    pending.add(user.idLong)
                    user.openPrivateChannel().queue { privateChannel: PrivateChannel ->
                        privateChannel.sendMessage("What is you Minecraft username?").queue() }
                }
            }

            event.reaction.removeReaction(user).queueAfter(1, TimeUnit.SECONDS)
        }
    }

    override fun onPrivateMessageReceived(@Nonnull event: PrivateMessageReceivedEvent) {
        val user = event.author
        if (user.isBot || user.isFake) return
        if (!pending.contains(user.idLong)) {
            logger.warn("Received Private Message from user without Linking Request")
            return
        }

        pending.remove(user.idLong)
        val username = event.message.contentRaw.split(" ").toTypedArray()[0]
        val eventContext = EventContext.of(mapOf(NicoConstants.DISCORD_CHANNEL to event.channel, NicoConstants.DISCORD_USER to event.author))
        Sponge.getEventManager().post(LinkRequestEvent(username, Cause.of(eventContext, this)))
    }

    @Listener
    private fun onUserAccepted(event: UserAcceptedEvent) {
        logger.info("Received User Accepted Event. Removing from pending set")
        pending.remove(event.userId)
    }
}