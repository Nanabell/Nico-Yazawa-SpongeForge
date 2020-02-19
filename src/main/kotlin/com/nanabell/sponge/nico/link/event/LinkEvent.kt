package com.nanabell.sponge.nico.link.event

import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent

open class LinkEvent(private val cause: Cause) : AbstractEvent() {

    override fun getCause(): Cause {
        return cause
    }

}