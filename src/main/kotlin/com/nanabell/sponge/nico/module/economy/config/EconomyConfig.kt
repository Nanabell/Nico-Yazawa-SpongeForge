package com.nanabell.sponge.nico.module.economy.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class EconomyConfig(

        @Setting("create-accounts", comment = "Should the Economy Service create accounts if they do not yet exist?")
        val create: Boolean = false

) : Config
