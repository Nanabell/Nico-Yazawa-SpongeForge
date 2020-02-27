package com.nanabell.sponge.nico.module.economy.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import java.math.BigDecimal
import java.util.*

@Entity("Minecraft.Economy.Account", noClassnameStored = true)
data class MakiStorage(

        @Indexed
        val uuid: UUID,

        val balance: BigDecimal

) : DataEntry {
        @Suppress("unused")
        private constructor() : this(UUID.randomUUID(), BigDecimal.ZERO)
}