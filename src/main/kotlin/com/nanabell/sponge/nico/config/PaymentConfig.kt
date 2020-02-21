package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class PaymentConfig(paymentAmount: Int = 50, dailyPaymentLimit: Int = 1500, paymentChance: Int = 100, requiredPermission: String = "") {
    @Setting(value = "payment-amount", comment = "Amount Money that is rewarded [0 - 2,147,483,647]")
    var paymentAmount = paymentAmount
        get() = field.coerceAtLeast(0)
        private set

    @Setting(value = "payment-limit", comment = "Maximum amount of currency that can be earned per day [0 = no limit, 2,147,483,647 = max]")
    var dailyPaymentLimit = dailyPaymentLimit
        get() = field.coerceAtLeast(0)
        private set

    @Setting(value = "payment-chance", comment = "Percentage chance for payment [0 = never, 100 = always]")
    var paymentChance = paymentChance
        get() = field.coerceIn(0, 100)
        private set

    @Setting(value = "required-permission", comment = "Required Permission to be eligible for this Payment [Empty for no requirement]")
    var requiredPermission: String = requiredPermission
        private set

    companion object {

        @JvmStatic
        fun defaults(): List<PaymentConfig> {
            return listOf(PaymentConfig(150, 0, 30, "nico.donator"),
                    PaymentConfig())
        }
    }
}