package com.nanabell.sponge.nico.module.economy.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import java.math.BigDecimal

@Entity("activity.userdatas", noClassnameStored = true)
data class NicoStorage(
        @Indexed
        val userId: String,

        private var __v: Int = 0,

        private var rank: Int = 0,

        @Indexed
        private var score: Int

) : DataEntry {

    @Suppress("unused")
    private constructor() : this("", 0, -1, -1)

    var balance: BigDecimal
        get() = BigDecimal(score)
        set(value) = value.run { score = value.toInt() }
}