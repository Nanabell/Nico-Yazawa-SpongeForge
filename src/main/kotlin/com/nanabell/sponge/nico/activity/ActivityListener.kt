package com.nanabell.sponge.nico.activity

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.orNull
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.action.InteractEvent
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

class ActivityListener(plugin: NicoYazawa, private val service: ActivityService) {

    init {
        Sponge.getEventManager().registerListeners(plugin, this)
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        service.getPlayer(event.targetEntity).forceStopAFK()
    }

    @Listener
    fun onPlayerChatMessage(event: MessageChannelEvent.Chat) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull() ?: return
        service.getPlayer(player).interact(event.cause)
    }

    @Listener
    fun onPlayerCommand(event: SendCommandEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull() ?: return
        service.getPlayer(player).interact(event.cause)
    }

    @Listener
    fun onPlayerInteract(event: InteractEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull() ?: return
        service.getPlayer(player).interact(event.cause)
    }

    @Listener
    fun onPlayerMove(event: MoveEntityEvent) {
        val player = event.context[EventContextKeys.NOTIFIER].orNull() ?: return
        if (event.fromTransform.rotation == event.toTransform.rotation) return

        service.getPlayer(player).interact(event.cause)
    }

}