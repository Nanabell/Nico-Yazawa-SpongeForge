package com.nanabell.sponge.nico.module.link.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.event.*
import com.nanabell.sponge.nico.module.link.service.LinkService
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

@RegisterListener
class UsernameRequestListener : AbstractListener<LinkModule>() {

    private val linkService: LinkService = NicoYazawa.getPlugin().getServiceRegistry().provideUnchecked()
    private val pending: MutableSet<Long> = HashSet()

    override fun onReady() {
        val config = module.getConfigOrDefault()
        val discordService: DiscordService = NicoYazawa.getPlugin().getServiceRegistry().provide() ?: return
        discordService.registerListener(this)

        val channel = discordService.getTextChannel(config.channelId) ?: return
        val message = channel.getHistoryAround(config.messageId, 1).complete().getMessageById(config.messageId)
        if (message != null && message.reactions.isEmpty()) {
            message.addReaction(config.reactionEmote).queue {
                logger.info("Added initial reaction ${config.reactionEmote} to message ${message.id} in channel ${channel.name}")
            }
        }
    }

    @SubscribeEvent
    fun onGuildMessageReactionAdd(@Nonnull event: GuildMessageReactionAddEvent) {
        val user = event.user
        if (user.isBot || user.isFake) return

        val config = module.getConfigOrDefault()
        if (event.messageIdLong == config.messageId) {
            if (event.reactionEmote.asCodepoints == config.reactionEmote) {

                if (!linkService.isPending(user)) {
                    pending.add(user.idLong)
                    user.openPrivateChannel().queue { privateChannel: PrivateChannel ->
                        privateChannel.sendMessage("What is you Minecraft username?").queue()
                    }
                }
            }

            event.reaction.removeReaction(user).queueAfter(1, TimeUnit.SECONDS)
        }
    }

    @SubscribeEvent
    fun onPrivateMessageReceived(@Nonnull event: PrivateMessageReceivedEvent) {
        val user = event.author
        if (user.isBot || user.isFake) return
        if (!pending.contains(user.idLong)) {
            logger.warn("Received Private Message from user without Linking Request")
            return
        }

        Sponge.getEventManager().post(LinkRequestEvent(event.message.contentRaw, user, Cause.of(EventContext.empty(), this)))
    }

    @Listener
    fun onUserFound(event: UsernameFoundEvent) {
        pending.remove(event.source.idLong)

        event.source.openPrivateChannel().queue {
            it.sendMessage("A Requests has been Sent to the User `${event.username}`.\nPlease click confirm InGame...")
        }
    }

    @Listener
    fun onLinkRequestFailed(event: LinkRequestFailedEvent) {
        val message = when (event) {
            is UsernameNotFoundEvent -> "`${event.username}` is not online.\nProvide the name of an online player."
            is AlreadyPendingEvent -> "There is already a Pending request for `${event.source.asTag}`"
            is AlreadyLinkedEvent -> "The Account `${event.source.asTag}` is already linked to `${event.target.name}`"
            else -> "Unknown LinkRequest Failure. Cause: `${event.cause}`\nContext: ```\n${event.context}```"
        }

        event.source.openPrivateChannel().queue {
            it.sendMessage(message).queue()
        }
    }
}