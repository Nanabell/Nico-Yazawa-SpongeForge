package com.nanabell.sponge.nico.internal.event

import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.cause.Cause

abstract class StandardCancellableEvent(cause: Cause) : StandardEvent(cause), Cancellable {

    private var isCanceled = false

    override fun setCancelled(cancel: Boolean) {
        this.isCanceled = cancel
    }

    override fun isCancelled(): Boolean = isCancelled
}