package com.nanabell.sponge.nico.internal.event

import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent

abstract class StandardEvent(private val cause: Cause) : AbstractEvent() {

    override fun getCause(): Cause = cause

}
