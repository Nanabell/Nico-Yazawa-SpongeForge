package com.nanabell.sponge.nico.discord.event

import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

class UserAcceptedEvent(val userId: Long, private val cause: Cause) : Event {

    override fun getCause(): Cause {
        return cause
    }
}