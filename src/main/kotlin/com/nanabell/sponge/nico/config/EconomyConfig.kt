package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class EconomyConfig {

    @Setting("create", comment = "Should The plugin create economy accounts if they do not yet exist?")
    var createAccounts: Boolean = false
        private set

}