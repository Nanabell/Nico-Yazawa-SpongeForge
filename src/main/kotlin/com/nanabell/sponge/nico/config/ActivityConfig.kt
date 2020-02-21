package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class ActivityConfig(

        @Setting("enabled")
        val enabled: Boolean = true,

        @Setting(value = "disabled_worlds", comment = "List of Worlds where payments are disabled")
        val disabledWorlds: List<String> = listOf("creative_world"),

        @Setting(value = "afkTimeout", comment = "After how many seconds is a user considered AFK? [default: 180 (2min), 0 <= no timeout]")
        val afkTimeout: Long = 2 * 60,

        @Setting(value = "payments", comment = "Payment Settings. Can have multiple. If multiple match only the first match will be used. You can use the chance setting for some randomness")
        val paymentConfigs: List<PaymentConfig> = PaymentConfig.defaults()
)
