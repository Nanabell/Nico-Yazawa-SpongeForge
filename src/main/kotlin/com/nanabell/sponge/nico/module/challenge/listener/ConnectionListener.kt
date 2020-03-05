package com.nanabell.sponge.nico.module.challenge.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.challenge.ChallengeModule
import com.nanabell.sponge.nico.module.challenge.service.ChallengeService
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class ConnectionListener : AbstractListener<ChallengeModule>() {

    private val service: ChallengeService = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        service.loadPlayer(event.targetEntity)
    }

    @Listener
    fun onPlayerLeave(event: ClientConnectionEvent.Disconnect) {
        service.savePlayer(event.targetEntity)
    }

}