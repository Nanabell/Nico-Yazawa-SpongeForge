package com.nanabell.sponge.nico.module.sync.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class KickConfig(

        @Setting("enabled", comment = "Is the AutoKick Watchdog enabled?")
        val enabled: Boolean = true,

        @Setting("kick-interval", comment = "How often should the Watchdog run (in seconds)")
        private val _kickInterval: Long = 5 * 60,

        @Setting("kick-playtime", comment = "How long until an unlinked account becomes eligible for auto kicking? (in seconds)")
        private val _kickPlaytime: Long = 5 * 60,

        @Setting("kick-information", comment = "The Kick message that will display why he was kicked")
        val information: String = ""

) : Config {

    val kickInterval get() = _kickInterval.coerceAtLeast(60)

    val kickPlaytime get() = _kickPlaytime.coerceAtLeast(60)

}
