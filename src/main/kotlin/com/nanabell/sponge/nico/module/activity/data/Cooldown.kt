package com.nanabell.sponge.nico.module.activity.data

import java.time.Duration
import java.time.Instant
import java.util.*

data class Cooldown(val uniqueId: UUID, private val cooldown: Duration) {

    private var start: Instant = Instant.now()
    private var elapsed: Duration = Duration.ZERO
    private val _remaining: Duration get() = cooldown.minus(elapsed)

    private var paused: Boolean = false

    fun pause() {
        update()

        paused = true
    }

    fun resume() {
        paused = false
        start = Instant.now()

        update()
    }

    fun getRemaining(): Duration {
        update()

        return _remaining.coerceAtLeast(Duration.ZERO)
    }

    private fun update() {
        if (!paused) {
            elapsed = elapsed.plus(Duration.between(start, Instant.now()))

            start = Instant.now()
        }
    }
}