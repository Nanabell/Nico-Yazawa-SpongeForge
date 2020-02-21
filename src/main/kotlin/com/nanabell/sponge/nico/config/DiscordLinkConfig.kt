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
        linkGroup: String = ""
) {

    @Setting(comment = "Discord Bot Token")
    var token = token
        private set

    @Setting(value = "link_guild_id", comment = "Snowflake Id of the Discord Guild")
    var guildId = guildId
        private set

    @Setting(value = "link_channel_id", comment = "Snowflake Id of the Discord Channel")
    var channelId = channelId
        private set

    @Setting(value = "link_message_id", comment = "Snowflake ID of the Discord Message")
    var messageId = messageId
        private set

    @Setting(value = "reaction_emote", comment = "Reaction Emote which will be used to link Accounts")
    var reactionEmote = reactionEmote
        private set

    @Setting(value = "link_role", comment = "The Discord Role that should be given if an account was linked successfully (-1 to disable)")
    var linkRole = linkRole
        private set

    @Setting(value = "link_group", comment = "The Minecraft Permission Group that should be awarded upon successful linking (empty to disable)")
    var linkGroup: String = linkGroup
        private set
}