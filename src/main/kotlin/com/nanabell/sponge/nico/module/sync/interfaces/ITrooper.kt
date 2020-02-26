package com.nanabell.sponge.nico.module.sync.interfaces

import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.module.sync.data.Troop

interface ITrooper {

    fun exists(player: MinecraftUser): Boolean

    fun hasTroop(player: MinecraftUser, troop: String): Boolean

    fun getTroops(player: MinecraftUser): List<Troop>

    fun addTroop(player: MinecraftUser, troop: Troop)

    fun removeTroop(player: MinecraftUser, troop: Troop)

}
