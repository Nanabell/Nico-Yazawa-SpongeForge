package com.nanabell.sponge.nico.link.sync

import com.nanabell.sponge.nico.extensions.MinecraftUser

interface ITrooper {

    fun exists(player: MinecraftUser): Boolean

    fun hasTroop(player: MinecraftUser, troop: String): Boolean

    fun getTroops(player: MinecraftUser): List<Troop>

    fun addTroop(player: MinecraftUser, troop: Troop)

    fun removeTroop(player: MinecraftUser, troop: Troop)

}
