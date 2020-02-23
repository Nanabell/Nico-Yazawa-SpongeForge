package com.nanabell.sponge.nico.config

import com.nanabell.sponge.nico.link.sync.Troop
import com.nanabell.sponge.nico.link.sync.TroopSource
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class DiscordLinkConfig(

        @Setting(comment = "Discord Bot Token")
        val token: String = "DISCORD_TOKEN_HERE",

        @Setting(value = "link-guild", comment = "Snowflake Id of the Discord Guild")
        val guildId: Long = -1L,

        @Setting(value = "link-channel", comment = "Snowflake Id of the Discord Channel")
        val channelId: Long = -1L,

        @Setting(value = "link-message", comment = "Snowflake ID of the Discord Message")
        val messageId: Long = -1L,

        @Setting(value = "reaction-emote", comment = "Reaction Emote which will be used to link Accounts")
        val reactionEmote: String = "U+1f517",

        @Setting(value = "link-role", comment = "The Discord Role that should be given if an account was linked successfully (-1 to disable)")
        val linkRole: Long = -1L,

        @Setting(value = "link-group", comment = "The Minecraft Permission Group that should be awarded upon successful linking (empty to disable)")
        val linkGroup: String = "",

        @Setting("kick-unlinked")
        val kickUnlinked: Boolean = true,

        @Setting("kick-interval")
        private val _kickInterval: Long = 5 * 60,

        @Setting("kick-max-playtime")
        private val _kickPlaytime: Long = 5 * 60,

        @Setting("kick-refer-channel")
        val kickReferChannel: String = "<INSERT-CHANNEL-NAME-HERE>",

        @Setting("sync")
        val syncConfig: SyncConfig = SyncConfig()

) {
    val kickInterval get() = _kickInterval.coerceAtLeast(60)
    val kickPlaytime get() = _kickPlaytime.coerceAtLeast(60)

    @ConfigSerializable
    data class SyncConfig(

            @Setting("sync-discord", comment = "Sync Discord Roles to Minecraft Permission?")
            val discordSync: Boolean = true,

            @Setting("sync-minecraft", comment = "Sync Minecraft Permissions to Discord Roles?")
            val minecraftSync: Boolean = true,

            @Setting("troops", comment = "Troops which define Syncing rules. Look at examples for Pattern")
            private val _troops: List<String> = listOf()
    ) {

        private val troops: Map<TroopSource, List<Troop>>

        init {
            troops = _troops.mapNotNull {
                val split = when {
                    it.contains("==>") -> Pair(TroopSource.MINECRAFT, it.split("==>"))
                    it.contains("<==") -> Pair(TroopSource.DISCORD, it.split("<=="))
                    else -> return@mapNotNull null
                }

                Troop(split.second[0], split.second[1], split.first)
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
}
