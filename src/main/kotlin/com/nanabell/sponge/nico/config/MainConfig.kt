package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class MainConfig(
        databaseUrl: String = "jdbc:sqlite:nicos-office.db",
        activityConfig: ActivityConfig = ActivityConfig(),
        discordLinkConfig: DiscordLinkConfig = DiscordLinkConfig()
) {

    @Setting(comment = "Database URL used by Nico to store immediate data")
    var databaseUrl = databaseUrl
        private set

    @Setting(value = "activity", comment = "Nico Points Activity Settings. Gain NicoPoints by being active in Minecraft")
    var activityConfig = activityConfig
        private set

    @Setting(value = "discord", comment = "Discord Linking Settings. \"Authenticate\" Minecraft users with Discord")
    var discordLinkConfig = discordLinkConfig
        private set
}