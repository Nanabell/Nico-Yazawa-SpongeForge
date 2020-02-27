package com.nanabell.sponge.nico.module.activity.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import dev.morphia.annotations.Property
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@Entity("Minecraft.Playtime", noClassnameStored = true)
class PlaytimeData(

        @Indexed
        val userId: UUID,

        @Property("playtime")
        private val _playtime: Long

) : DataEntry {
    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    fun getPlaytime(): Duration {
        return Duration.of(_playtime, ChronoUnit.SECONDS)
    }
}