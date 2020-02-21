package com.nanabell.sponge.nico.activity.event

import com.nanabell.sponge.nico.activity.ActivityPlayer
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

class PlayerActiveEvent(val player: ActivityPlayer, private val cause: Cause) : Event, Cancellable {

    private var isCancelled = false

    override fun getCause(): Cause = cause

    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }



}
