package com.nanabell.sponge.nico.module.sync.config

import com.nanabell.sponge.nico.internal.config.Config
import com.nanabell.sponge.nico.module.sync.data.Troop
import com.nanabell.sponge.nico.module.sync.misc.TroopSource
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class SyncConfig(

        @Setting("discord-sync", comment = "Sync Discord Roles to Minecraft Permissions?")
        val discordSync: Boolean = true,

        @Setting("minecraft-sync", comment = "Sync Minecraft Permissions to Discord Roles?")
        val minecraftSync: Boolean = true,

        @Setting("troops", comment = "Syncing Rules. [example: roleId==>permission | roleId<==permission] arrow direction will determinate source\n target will be given is user has source")
        private val _troops: List<String> = listOf(),

        @Setting("auto-kick", comment = "Automatically Kick users who dont Link their Accounts within a settable TimeFrame")
        val kickConfig: KickConfig = KickConfig()

) : Config {
    private val troops: Map<TroopSource, List<Troop>> by lazy {
        _troops.mapNotNull {
            val split = when {
                it.contains("==>") -> Pair(TroopSource.DISCORD, it.split("==>"))
                it.contains("<==") -> Pair(TroopSource.MINECRAFT, it.split("<=="))
                else -> return@mapNotNull null
            }

            if (split.second.size != 2)
                return@mapNotNull null

            if (split.second[0].toLongOrNull() == null)
                return@mapNotNull null

            Troop(split.second[0].toLong(), split.second[1], split.first)
        }.groupBy { it.source }
    }

    fun getMinecraftTroops(): List<Troop> {
        return troops[TroopSource.MINECRAFT] ?: emptyList()
    }

    fun getDiscordTroops(): List<Troop> {
        return troops[TroopSource.DISCORD] ?: emptyList()
    }

    fun getTroopsFrom(source: TroopSource): List<Troop> {
        return troops[source] ?: emptyList()
    }
}