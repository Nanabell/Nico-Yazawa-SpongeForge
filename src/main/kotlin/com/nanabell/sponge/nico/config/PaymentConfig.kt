package com.nanabell.sponge.nico.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class PaymentConfig(

        @Setting(value = "payment-amount", comment = "Amount Money that is rewarded [0 - 2,147,483,647]")
        private val _paymentAmount: Long = 50,

        @Setting(value = "payment-limit", comment = "Maximum amount of currency that can be earned per day [0 = no limit, 2,147,483,647 = max]")
        private val _dailyPaymentLimit: Long = 1500,

        @Setting(value = "payment-chance", comment = "Percentage chance for payment [0 = never, 100 = always]")
        private val _paymentChance: Int = 100,

        @Setting(value = "required-permission", comment = "Required Permission to be eligible for this Payment [Empty for no requirement]")
        val requiredPermission: String = ""

      ) {
        val paymentAmount get() = _paymentAmount.coerceAtLeast(0)
        val dailyPaymentLimit get() = _dailyPaymentLimit.coerceAtLeast(0)
        val paymentChance get() = _paymentChance.coerceIn(0, 100)

    companion object {

        @JvmStatic
        fun defaults(): List<PaymentConfig> {
            return listOf(PaymentConfig(150, 0, 30, "nico.donator"),
                    PaymentConfig())
        }
    }
}