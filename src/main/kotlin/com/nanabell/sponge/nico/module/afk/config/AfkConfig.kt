package com.nanabell.sponge.nico.module.afk.config

import com.nanabell.quickstart.config.ModuleConfig
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration
import java.time.temporal.ChronoUnit

@ConfigSerializable
data class AfkConfig(

        @Setting("enabled", comment = "Enable the Afk System?")
        val enabled: Boolean = true,

        @Setting("afk-timeout", comment = "Amount of time before somone is flagged as AFK")
        private val _afkTimeout: Long = 1800

) : ModuleConfig {
        val afkTimeout: Duration get() = Duration.of(_afkTimeout, ChronoUnit.SECONDS)
}
