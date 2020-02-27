package com.nanabell.sponge.nico.module.activity.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting

data class RewardConfig(

        @Setting("currency", comment = "Name of the currency to use")
        val currency: String = "Nico Points",

        @Setting("amount", comment = "How much should be awarded?")
        private val _amount: Long = 150,

        @Setting("limit", comment = "How many times can this reward be paid out?")
        private val _limit: Int = 0,

        @Setting("chance", comment = "Whats the Reward chance in %?")
        private val _chance: Int = 100,

        @Setting("required-permission", comment = "What Permission is required to be eligible for this Reward")
        val requiredPermission: String = "",

        @Setting("bonus-permission", comment = "Permission Requirement for the additional bonus amount")
        val bonusPermission: String = "",

        @Setting("bonus-amount", comment = "Will be added on top of the regular reward if the user has the bonus-permission")
        private val _bonusAmount: Long = 0

) : Config {

    val amount = _amount.coerceAtLeast(0)

    val limit = _limit.coerceAtLeast(-1)

    val chance = _chance.coerceIn(0, 100)

    val bonusAmount = _bonusAmount.coerceAtLeast(0)

}