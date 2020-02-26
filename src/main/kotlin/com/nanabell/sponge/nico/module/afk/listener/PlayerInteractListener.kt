package com.nanabell.sponge.nico.module.afk.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.service.AfkService
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.action.InteractEvent
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class PlayerInteractListener : AbstractListener<AfkModule>() {

    private val service: AfkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        service.stopAfk(event.targetEntity, event.cause, true)
    }

    @Listener
    fun onPlayerChatMessage(event: MessageChannelEvent.Chat) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull()?.player?.orNull() ?: return
        service.interact(player, event.cause)
    }

    @Listener
    fun onPlayerCommand(event: SendCommandEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull()?.player?.orNull()?: return
        service.interact(player, event.cause)
    }

    @Listener
    fun onPlayerInteract(event: InteractEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull()?.player?.orNull() ?: return
        service.interact(player, event.cause)
    }

    @Listener
    fun onPlayerMove(event: MoveEntityEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull()?.player?.orNull() ?: return
        if (event.fromTransform.rotation == event.toTransform.rotation) return

        service.interact(player, event.cause)
    }

}