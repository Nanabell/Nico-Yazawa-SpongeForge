package com.nanabell.sponge.nico.activity.event

import com.nanabell.sponge.nico.activity.ActivityPlayer
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider

object ActivityContextKeys {

    val INACTIVE = createFor<Long>("INACTIVE_SINCE")
    val PLAYER = createFor<ActivityPlayer>("ACTIVITY_PLAYER")

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFor(id: String): EventContextKey<T> {
        return DummyObjectProvider.createFor<EventContextKey<*>>(EventContextKey::class.java, id) as EventContextKey<T>
    }
}