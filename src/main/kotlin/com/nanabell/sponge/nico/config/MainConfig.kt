package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class MainConfig(

        @Setting(comment = "Database URL used by Nico to store immediate data")
        val databaseUrl: String = "mongodb://user:password@host/database",

        @Setting(value = "activity", comment = "Nico Points Activity Settings. Gain NicoPoints by being active in Minecraft")
        val activityConfig: ActivityConfig = ActivityConfig(),

        @Setting(value = "discord", comment = "Discord Linking Settings. \"Authenticate\" Minecraft users with Discord")
        val discordLinkConfig: DiscordLinkConfig = DiscordLinkConfig(),

        @Setting(value = "economy")
        val economyConfig: EconomyConfig = EconomyConfig()

)
