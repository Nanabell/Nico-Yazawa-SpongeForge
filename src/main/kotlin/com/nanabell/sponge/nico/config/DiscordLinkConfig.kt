package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class DiscordLinkConfig(
        token: String = "DISCORD_TOKEN_HERE",
        guildId: Long = -1L,
        channelId: Long = -1L,
        messageId: Long = -1L,
        reactionEmote: String = "U+1f517",
        linkRole: Long = -1L,
        linkGroup: String = "",
        kickUnlinked: Boolean = true,
        kickInterval: Long = 5,
        kickPlaytime: Long = 5,
        kickReferChannel: String = "<INSERT-CHANNEL-NAME-HERE>"
) {

    @Setting(comment = "Discord Bot Token")
    var token = token
        private set

    @Setting(value = "link-guild", comment = "Snowflake Id of the Discord Guild")
    var guildId = guildId
        private set

    @Setting(value = "link-channel", comment = "Snowflake Id of the Discord Channel")
    var channelId = channelId
        private set

    @Setting(value = "link-message", comment = "Snowflake ID of the Discord Message")
    var messageId = messageId
        private set

    @Setting(value = "reaction-emote", comment = "Reaction Emote which will be used to link Accounts")
    var reactionEmote = reactionEmote
        private set

    @Setting(value = "link-role", comment = "The Discord Role that should be given if an account was linked successfully (-1 to disable)")
    var linkRole = linkRole
        private set

    @Setting(value = "link-group", comment = "The Minecraft Permission Group that should be awarded upon successful linking (empty to disable)")
    var linkGroup: String = linkGroup
        private set

    @Setting("kick-unlinked")
    var kickUnlinked: Boolean = kickUnlinked
        private set

    @Setting("kick-interval")
    var kickInterval: Long = kickInterval
        private set

    @Setting("kick-max-playtime")
    var kickPlaytime: Long = kickPlaytime
        private set

    @Setting("kick-refer-channel")
    var kickReferChannel = kickReferChannel
        private set
}