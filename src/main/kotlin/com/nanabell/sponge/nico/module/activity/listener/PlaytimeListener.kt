package com.nanabell.sponge.nico.module.activity.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class PlaytimeListener : AbstractListener<ActivityModule>() {

    private val playtimeService: PlaytimeService = NicoYazawa.getPlugin().getServiceRegistry().provideUnchecked()

    @Listener
    private fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        playtimeService.startSession(event.targetEntity)
    }

    @Listener
    private fun onPlayerDisconnect(event: ClientConnectionEvent.Disconnect) {
        playtimeService.endSession(event.targetEntity)
    }

    @Listener
    private fun onPlayerResume(event: PlayerActiveEvent) {
        playtimeService.addAfkDuration(event.targetEntity, event.getAfkDuration())
    }
}