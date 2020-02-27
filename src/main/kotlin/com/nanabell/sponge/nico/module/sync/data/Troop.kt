package com.nanabell.sponge.nico.module.sync.data

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import com.nanabell.sponge.nico.module.sync.misc.TroopSource

data class Troop(
        val role: Long,
        val permission: String,
        val source: TroopSource
) {

    private val discordService: DiscordService? by lazy { NicoYazawa.getServiceRegistry().provide<DiscordService>() }

    fun getFromSource(source: TroopSource) = if (source == TroopSource.MINECRAFT) permission else role.toString()
    fun getRoleName(): String = discordService?.getRole(role)?.name ?: "[ERR] Role ($role) not found!"

    override fun toString(): String {
        return (if (source == TroopSource.MINECRAFT) "Troop: [group $permission ==> role $role, source: $source"
        else "Troop: [role $role ==> group $permission, source: $source") + "]"
    }
}