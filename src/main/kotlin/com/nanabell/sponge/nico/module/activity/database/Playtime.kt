package com.nanabell.sponge.nico.module.activity.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import dev.morphia.annotations.Property
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Entity("Minecraft.Playtime", noClassnameStored = true)
data class Playtime(

        @Id
        val uniqueId: UUID

) : DataEntry {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID())

    @Property("play_time")
    private var _totalPlayTime: Long = 0

    @Property("afk_time")
    private var _totalAfkTime: Long = 0

    @Transient
    private var sessionPlayTime: Duration = Duration.ZERO

    @Transient
    private var sessionAfkTime: Duration = Duration.ZERO

    @Transient
    private var lastChange: Instant = Instant.now()

    @Transient
    private var isAfk = false


    fun getPlayTime(): Duration {
        return getPlayTimeInternal().plus(sessionPlayTime)
    }

    fun getAfkTime(): Duration {
        return getAfkTimeInternal().plus(sessionAfkTime)
    }

    fun getActiveTime(): Duration {
        return getPlayTime().minus(getAfkTime())
    }

    fun getSessionPlayTime(): Duration {
        return sessionPlayTime
    }

    fun getSessionAfkTime(): Duration {
        return sessionAfkTime
    }

    fun getSessionActiveTime(): Duration {
        return getSessionPlayTime().minus(getSessionAfkTime())
    }

    fun setAfk() {
        update()

        isAfk = true

    }

    fun setActive() {
        update()

        isAfk = false
    }

    fun endSession() {
        update()

        setPlayTimeInternal(getPlayTime())
        setAfkTimeInternal(getAfkTime())

        sessionPlayTime = Duration.ZERO
        sessionAfkTime = Duration.ZERO
    }

    private fun getPlayTimeInternal(): Duration {
        return Duration.of(_totalPlayTime, ChronoUnit.SECONDS)
    }

    private fun setPlayTimeInternal(playTime: Duration) {
        _totalPlayTime = playTime.seconds
    }

    private fun getAfkTimeInternal(): Duration {
        return Duration.of(_totalAfkTime, ChronoUnit.SECONDS)
    }

    private fun setAfkTimeInternal(afkTime: Duration) {
        _totalAfkTime = afkTime.seconds
    }

    fun update() {
        val duration = Duration.between(lastChange, Instant.now())

        if (isAfk) {
            sessionAfkTime = sessionPlayTime.plus(duration)
        }

        sessionPlayTime = sessionPlayTime.plus(duration)
        lastChange = Instant.now()
    }
}