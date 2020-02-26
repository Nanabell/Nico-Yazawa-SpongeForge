package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class MainConfig(

        @Setting(value = "activity", comment = "Nico Points Activity Settings. Gain NicoPoints by being active in Minecraft")
        val activityConfig: ActivityConfig = ActivityConfig()
)
