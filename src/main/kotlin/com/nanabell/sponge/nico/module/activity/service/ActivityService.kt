package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig
import com.nanabell.sponge.nico.module.activity.config.RewardConfig
import org.spongepowered.api.entity.living.player.Player
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

@RegisterService
class ActivityService : AbstractService<ActivityModule>() {

    private val rewardCounter: MutableMap<UUID, Int> = HashMap()
    private val lastRewards: MutableMap<UUID, Instant> = HashMap()

    private lateinit var config: ActivityConfig

    override fun onPreEnable() {
        config = module.getConfigOrDefault()
    }

    fun isOnCooldown(player: Player): Boolean {
        val last = lastRewards[player.uniqueId] ?: return false

        return last.plus(config.rewardCooldown) > Instant.now()
    }

    fun setOnCooldown(player: Player) {
        lastRewards[player.uniqueId] = Instant.now()
    }

    fun resetRewardCounter() {
        rewardCounter.clear()
    }

    // TODO: Move this somewhere else...
    fun checkRewardContext(rewardConfig: RewardConfig, player: Player): Boolean {
        return checkPermission(rewardConfig, player)
                && checkLimit(rewardConfig, player)
                && checkChance(rewardConfig, player)
    }

    private fun checkPermission(rewardConfig: RewardConfig, player: Player): Boolean {
        if (rewardConfig.requiredPermission.isNotEmpty()) {
            if (player.hasPermission(rewardConfig.requiredPermission))
                return true
        }

        return false
    }

    private fun checkLimit(rewardConfig: RewardConfig, player: Player): Boolean {
        if (rewardConfig.limit > 0 && rewardCounter.containsKey(player.uniqueId)) {
            if (rewardCounter[player.uniqueId] ?: 0 > rewardConfig.limit)
                return true
        }

        return false
    }

    private fun checkChance(rewardConfig: RewardConfig, player: Player): Boolean {
        if (rewardConfig.chance == 0) return false
        if (rewardConfig.chance == 100) return true

        return rewardConfig.chance > Random.nextInt(0, 101)
    }

}