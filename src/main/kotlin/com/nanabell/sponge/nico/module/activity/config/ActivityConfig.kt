package com.nanabell.sponge.nico.module.activity.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration
import java.time.temporal.ChronoUnit

@ConfigSerializable
data class ActivityConfig(

        @Setting("enable", comment = "Is the Activity Tracker Enabled?")
        val enabled: Boolean = true,

        @Setting("disabled-worlds", comment = "List of World-Names in which the Activity System will not work")
        val disabledWorlds: List<String> = listOf(),

        @Setting("reward-cooldown", comment = "How long a user is on cooldown adn cant receive rewards")
        private val _rewardCooldown: Long = 3600,

        @Setting("rewards", comment = "Rewards for being active")
        val rewards: List<RewardConfig> = listOf(RewardConfig(), RewardConfig())

) : Config {

        val rewardCooldown: Duration get() = Duration.of(_rewardCooldown.coerceAtLeast(0), ChronoUnit.SECONDS)

}
