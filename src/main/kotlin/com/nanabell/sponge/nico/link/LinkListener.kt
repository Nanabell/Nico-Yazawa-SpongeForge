package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import net.dv8tion.jda.api.entities.MessageChannel
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors

class LinkListener {

    private val logger = NicoYazawa.getLogger()
    private val discordService = Sponge.getServiceManager().provideUnchecked(DiscordService::class.java)
    private val linkService: LinkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)
    private val eventManager: EventManager = Sponge.getEventManager()

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        val context = event.cause.context
        val user = context.get(LinkEventContextKeys.USER).orNull()
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
}