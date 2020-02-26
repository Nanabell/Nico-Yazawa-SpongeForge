package com.nanabell.sponge.nico.module.core.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class CoreConfig(

        @Setting("startup-error", comment = "Error out on startup, to generate Config files but not Start the Server")
        val startupError: Boolean = true,

        @Setting("database-url", comment = "Mongodb DatabaseUrl ConnectionString for Authentication & Authorization")
        val databaseUrl: String = "mongodb://user:password@host/database",

        @Setting("database", comment = "Name of the Database to use")
        val database: String = "nico-yazawa"
) : Config
