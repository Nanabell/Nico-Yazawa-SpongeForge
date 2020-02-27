package com.nanabell.sponge.nico.module.activity.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import java.time.Duration
import java.util.*

@Entity("Minecraft.Playtime", noClassnameStored = true)
class PlaytimeData(

        @Indexed
        val userId: UUID,

        val playtime: Duration

) : DataEntry