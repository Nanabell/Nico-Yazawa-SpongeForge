package com.nanabell.sponge.nico.module.link.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.event.*
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors

@RegisterListener
class LinkRequestListener : AbstractListener<LinkModule>() {

    private val eventManager = Sponge.getEventManager()
    private val linkService = NicoYazawa.getPlugin().getServiceRegistry().provideUnchecked(LinkService::class)

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        if (linkService.isPending(event.source)) {
            eventManager.post(AlreadyPendingEvent(event.source, Cause.of(EventContext.empty(), this)))
            return
        }

        val player = Sponge.getServer().getPlayer(event.username).orNull()
        if (player == null) {
            eventManager.post(UsernameNotFoundEvent(event.username, event.source, Cause.of(EventContext.empty(), this)))
            return
        }

        if (linkService.isLinked(player)) {
            eventManager.post(AlreadyLinkedEvent(player, event.source, Cause.of(EventContext.empty(), this)))
            return
        }

        eventManager.post(UsernameFoundEvent(player.name, event.source, Cause.of(EventContext.empty(), this)))
        linkService.addPending(event.source, player)

        val msg: Text = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(event.source.asTag + " "))
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
}