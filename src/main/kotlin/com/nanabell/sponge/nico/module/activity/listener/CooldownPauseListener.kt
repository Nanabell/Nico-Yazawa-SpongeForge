package com.nanabell.sponge.nico.module.activity.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import com.nanabell.sponge.nico.module.afk.event.PlayerAFKEvent
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class CooldownPauseListener : AbstractListener<ActivityModule>() {

    private val activity: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onPlayerAfk(event: PlayerAFKEvent) {
        activity.pauseCooldown(event.targetEntity)
    }

    @Listener
    fun onPlayerActive(event: PlayerActiveEvent) {
        activity.resumeCooldown(event.targetEntity)
    }

    @Listener
    fun onPlayerLeave(event: ClientConnectionEvent.Disconnect) {
        activity.pauseCooldown(event.targetEntity)
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        activity.resumeCooldown(event.targetEntity)
    }
}
