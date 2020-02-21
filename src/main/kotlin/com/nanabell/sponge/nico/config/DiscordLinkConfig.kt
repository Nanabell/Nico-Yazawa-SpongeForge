package com.nanabell.sponge.nico.config

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
        val kickReferChannel: String = "<INSERT-CHANNEL-NAME-HERE>"
) {
        val kickInterval get() = _kickInterval.coerceAtLeast(60)
        val kickPlaytime get() = _kickPlaytime.coerceAtLeast(60)
}
