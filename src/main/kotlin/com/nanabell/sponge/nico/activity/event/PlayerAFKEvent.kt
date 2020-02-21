package com.nanabell.sponge.nico.activity.event

import com.nanabell.sponge.nico.activity.ActivityPlayer
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

class PlayerAFKEvent(val player: ActivityPlayer, private val cause: Cause) : Event, Cancellable {

    private var isCancelled = false

    override fun getCause(): Cause = this.cause

    override fun isCancelled(): Boolean  {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

}
