package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class ActivityConfig(disabledWorlds: List<String> = listOf("creative_world"), paymentInterval: Int = 600, paymentConfig: List<PaymentConfig> = PaymentConfig.defaults()) {

    @Setting(value = "disabled_worlds", comment = "List of Worlds where payments are disabled")
    var disabledWorlds: List<String> = disabledWorlds
        private set

    @Setting(value = "payment_interval", comment = "Interval in Seconds when payments occur [15 = min, 2,147,483,647 = max]")
    var paymentInterval = paymentInterval
        get() = field.coerceAtLeast(15)
        private set

    @Setting(value = "payments", comment = "Payment Settings. Can have multiple. If multiple match only the first match will be used. You can use the chance setting for some randomness")
    var paymentConfigs: List<PaymentConfig> = paymentConfig
        private set
}