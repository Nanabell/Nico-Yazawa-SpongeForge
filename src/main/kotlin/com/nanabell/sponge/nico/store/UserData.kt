package com.nanabell.sponge.nico.store

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import dev.morphia.annotations.Indexed
import java.util.*

@Entity("Activity.UserData", noClassnameStored = true)
data class UserData(
        @Indexed
        val userId: String,

        @Indexed
        var score: Int,

        var rank: Int = 0
) {
        @Suppress("unused")
        private constructor() : this("", -1, -1)
}