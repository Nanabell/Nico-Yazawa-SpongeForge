package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.extension.getOptionToLong
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig
import com.nanabell.sponge.nico.module.activity.config.RewardConfig
import com.nanabell.sponge.nico.module.activity.data.Cooldown
import org.spongepowered.api.entity.living.player.Player
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.random.Random

@RegisterService
class ActivityService : AbstractService<ActivityModule>() {

    private val rewardCounter: MutableMap<UUID, Int> = HashMap()
    private val cooldowns: MutableSet<Cooldown> = HashSet()

    private lateinit var config: ActivityConfig

    override fun onPreEnable() {
        config = module.getConfigOrDefault()
    }

    /**
     * Check if a User is currently on Activity Cooldown
     *
     * @return [Boolean] if on cooldown
     */
    fun isOnCooldown(player: Player): Boolean {
        return getCooldown(player) != Duration.ZERO
    }

    /**
     * Retrieve the Cooldown duration for a specific user.
     *
     * @return Duration for cooldown or [Duration.ZERO] ir not on cooldown or user not found
     */
    fun getCooldown(player: Player): Duration {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == player.uniqueId } ?: return Duration.ZERO // TODO: Set Users on Cooldown when they join?
        return cooldown.getRemaining()
    }

    /**
     * (Re)-Start the cooldown for a player.
     * This will use either the "nico.activity.cooldown" option or the config option as fallback
     *
     * @param player Player in question
     */
    fun startCooldown(player: Player) {
        cooldowns.removeIf { it.uniqueId == player.uniqueId }
        cooldowns.add(Cooldown(player.uniqueId, getRewardCooldown(player)))
    }

    /**
     * Pause the cooldown for a given player
     *
     * @param player player in question
     */
    fun pauseCooldown(player: Player) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == player.uniqueId } ?: return //TODO Handle
        cooldown.pause()
    }

    /**
     * Resume the cooldown for a given player
     *
     * @param player player in question
     */
    fun resumeCooldown(player: Player) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == player.uniqueId } ?: return //TODO Handle
        cooldown.resume()
    }

    private fun getRewardCooldown(player: Player): Duration {
        val seconds = player.getOptionToLong("nico.activity.cooldown", config.rewardCooldown.seconds)
        return if (seconds != null) Duration.of(seconds, ChronoUnit.SECONDS) else config.rewardCooldown
    }

    /**
     * Reset the reward Counter
     */
    fun resetRewardCounter() {
        rewardCounter.clear()
    }

    /**
     * Check a players Context if he is eligible for a reward
     */
    // TODO: Move this somewhere else...
    fun checkRewardContext(rewardConfig: RewardConfig, player: Player): Boolean {
        return checkPermission(rewardConfig, player)
                && checkLimit(rewardConfig, player)
                && checkChance(rewardConfig)
    }

    private fun checkPermission(rewardConfig: RewardConfig, player: Player): Boolean {
        if (rewardConfig.requiredPermission.isNotEmpty()) {
            if (!player.hasPermission(rewardConfig.requiredPermission))
                return false
        }

        return true
    }

    private fun checkLimit(rewardConfig: RewardConfig, player: Player): Boolean {
        if (rewardConfig.limit > 0 && rewardCounter.containsKey(player.uniqueId)) {
            if (rewardCounter[player.uniqueId] ?: 0 > rewardConfig.limit)
                return false
        }

        val count = rewardCounter.computeIfAbsent(player.uniqueId) { 0 } + 1
        rewardCounter[player.uniqueId] = count

        return true
    }

    private fun checkChance(rewardConfig: RewardConfig): Boolean {
        if (rewardConfig.chance == 0) return false
        if (rewardConfig.chance == 100) return true

        return rewardConfig.chance > Random.nextInt(0, 101)
    }

}