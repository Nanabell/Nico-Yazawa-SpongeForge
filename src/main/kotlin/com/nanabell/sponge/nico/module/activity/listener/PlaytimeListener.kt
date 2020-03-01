package com.nanabell.sponge.nico.module.activity.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.afk.event.PlayerAFKEvent
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class PlaytimeListener : AbstractListener<ActivityModule>() {

    private val service: PlaytimeService = NicoYazawa.getPlugin().getServiceRegistry().provideUnchecked()

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        service.startSession(event.targetEntity)
    }

    @Listener
    fun onPlayerDisconnect(event: ClientConnectionEvent.Disconnect) {
        service.endSession(event.targetEntity)
    }

    @Listener
    fun onPlayerAfk(event: PlayerAFKEvent) {
        service.setAfk(event.targetEntity)
    }

    @Listener
    fun onPlayerResume(event: PlayerActiveEvent) {
        service.setActive(event.targetEntity)
    }
}