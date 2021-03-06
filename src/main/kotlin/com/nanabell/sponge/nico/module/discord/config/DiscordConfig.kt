package com.nanabell.sponge.nico.module.discord.config

import com.nanabell.quickstart.config.ModuleConfig
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class DiscordConfig(

        @Setting("token", comment = "The Discord Bot token used to log into Discord")
        val token: String = "DISCORD_BOT_TOKEN",

        @Setting("guild-id", comment = "The Id of the Guild where the bot will work in")
        val guildId: Long = -1

) : ModuleConfig