package com.nanabell.sponge.nico.economy.storage

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import java.math.BigDecimal

@Entity("Activity.UserData", noClassnameStored = true)
data class NicoStorage(
        @Indexed
        val userId: String,

        @Indexed
        private var score: Int,

        private var rank: Int = 0
) {

    @Suppress("unused")
    private constructor() : this("", -1, -1)

    var balance: BigDecimal
        get() = BigDecimal(score)
        set(value) = value.run { score = value.toInt() }
}