package com.nanabell.sponge.nico.link.sync

import com.nanabell.sponge.nico.link.discord.DiscordService
import org.spongepowered.api.Sponge

data class Troop(
        val role: String,
        val permission: String,
        val source: TroopSource
) {

    private val discordService by lazy { Sponge.getServiceManager().provideUnchecked(DiscordService::class.java) }

    fun getFromSource(source: TroopSource) = if (source == TroopSource.MINECRAFT) permission else role
    fun getRoleName(): String = discordService.getRole(role)?.name ?: "[ERR] Role ($role) not found!"

    override fun toString(): String {
        return (if (source == TroopSource.MINECRAFT) "Troop: [group $permission ==> role $role, source: $source"
        else "Troop: [role $role ==> group $permission, source: $source") + "]"
    }
}