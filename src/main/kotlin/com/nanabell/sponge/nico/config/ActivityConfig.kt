package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class ActivityConfig(

        @Setting("enabled")
        val enabled: Boolean = true,

        @Setting(value = "disabled_worlds", comment = "List of Worlds where payments are disabled")
        val disabledWorlds: List<String> = listOf("creative_world"),

        @Setting(value = "payment_interval", comment = "Interval in Seconds when payments occur [15 = min, 2,147,483,647 = max]")
        private var _paymentInterval: Int = 600,

        @Setting(value = "payments", comment = "Payment Settings. Can have multiple. If multiple match only the first match will be used. You can use the chance setting for some randomness")
        val paymentConfigs: List<PaymentConfig> = PaymentConfig.defaults()
) {

    val paymentInterval = _paymentInterval.coerceAtLeast(10)
}
