package com.nanabell.sponge.nico.module.core.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.core.CoreModule
import com.nanabell.sponge.nico.module.core.service.PlaytimeService
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.time.Instant

@RegisterListener
class PlaytimeListener : AbstractListener<CoreModule>() {

    private val playtimeService: PlaytimeService = NicoYazawa.getPlugin().getServiceRegistry().provideUnchecked()

    @Listener
    private fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        playtimeService.add(event.targetEntity.uniqueId, Instant.now())
    }

    @Listener
    private fun onPlayerDisconnect(event: ClientConnectionEvent.Disconnect) {
        playtimeService.remove(event.targetEntity.uniqueId)
    }
}