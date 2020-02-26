package com.nanabell.sponge.nico.module.economy.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Embedded
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import org.spongepowered.api.service.context.Context
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

@Entity("Minecraft.Economy.Account", noClassnameStored = true)
data class MakiStorage(

        @Indexed
        val uuid: UUID,

        val balance: BigDecimal,

        @Embedded(concreteClass = HashSet::class)
        val contexts: Set<Context>
) : DataEntry {
        @Suppress("unused")
        private constructor() : this(UUID.randomUUID(), BigDecimal.ZERO, emptySet())
}