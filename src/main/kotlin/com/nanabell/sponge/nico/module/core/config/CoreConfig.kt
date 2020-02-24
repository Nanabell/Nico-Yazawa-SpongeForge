package com.nanabell.sponge.nico.module.core.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class CoreConfig (

        @Setting("startup-error", comment = "Error out on startup, to generate Config files but not Start the Server")
        val startupError: Boolean = true
) {

}