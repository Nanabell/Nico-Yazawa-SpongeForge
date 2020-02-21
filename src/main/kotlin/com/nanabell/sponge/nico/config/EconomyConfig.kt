package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class EconomyConfig(

        @Setting("create", comment = "Should The plugin create economy accounts if they do not yet exist?")
        val createAccounts: Boolean = false

)
