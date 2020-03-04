package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.extension.getOptionDuration
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig
import com.nanabell.sponge.nico.module.activity.config.RewardConfig
import com.nanabell.sponge.nico.module.activity.data.Cooldown
import com.nanabell.sponge.nico.module.activity.data.Payout
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import java.math.BigDecimal
import java.time.Duration
import kotlin.random.Random

@RegisterService
class ActivityService : AbstractService<ActivityModule>() {

    private val cooldowns: MutableSet<Cooldown> = HashSet()
    private val payouts: MutableList<Payout> = ArrayList()

    private lateinit var config: ActivityConfig

    override fun onPreEnable() {
        config = module.getConfigOrDefault()
    }

    /**
     * Check if a User is currently on Activity Cooldown
     *
     * @return [Boolean] if on cooldown
     */
    fun isOnCooldown(user: User): Boolean {
        return getCooldown(user) != Duration.ZERO
    }

    /**
     * Retrieve the Cooldown duration for a specific user.
     * Cooldown will pause on player leave
     *
     * @return Duration for cooldown or [Duration.ZERO] ir not on cooldown or user not found
     */
    fun getCooldown(user: User): Duration {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == user.uniqueId } ?: return Duration.ZERO
        return cooldown.getRemaining()
    }

    /**
     * (Re)-Start the cooldown for a player.
     * This will use either the "nico.activity.cooldown" option or the config option as fallback
     *
     * @param user Player in question
     */
    fun startCooldown(user: User) {
        cooldowns.removeIf { it.uniqueId == user.uniqueId }
        cooldowns.add(Cooldown(user.uniqueId, user.getOptionDuration(ActivityModule.O_ACTIVITY_COOLDOWN, config.rewardCooldown)))
    }

    /**
     * Pause the cooldown for a given player
     * If the player has no cooldown registered this request is quietly ignored
     *
     * @param user player in question
     */
    fun pauseCooldown(user: User) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == user.uniqueId } ?: return
        cooldown.pause()
    }

    /**
     * Resume the cooldown for a given player
     * If the player has no cooldown registered this request is quietly ignored
     *
     * @param user player in question
     */
    fun resumeCooldown(user: User) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == user.uniqueId } ?: return
        cooldown.resume()
    }

    /**
     * Remove the cooldown for a given player
     *
     * @param user player in question
     * @return [Boolean] if successful
     */
    fun removeCooldown(user: User): Boolean {
        return cooldowns.removeIf { it.uniqueId == user.uniqueId }
    }

    fun getPayoutAmount(user: User): BigDecimal {
        return payouts.filter { it.uniqueId == user.uniqueId }.sumByDouble { it.amount.toDouble() }.toBigDecimal()
    }

    fun getPayoutCount(user: User): Int {
        return payouts.filter { it.uniqueId == user.uniqueId }.count()
    }

    fun addPayout(user: User, amount: BigDecimal) {
        payouts.add(Payout(user.uniqueId, amount))
    }

    fun clearPayouts(user: User): Boolean {
        return payouts.removeIf { it.uniqueId == user.uniqueId }
    }

    fun clearPayouts() {
        payouts.clear()
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
        if (rewardConfig.limit > 0 && payouts.any { it.uniqueId == player.uniqueId }) {
            if (getPayoutCount(player) >= rewardConfig.limit)
                return false
        }

        return true
    }

    private fun checkChance(rewardConfig: RewardConfig): Boolean {
        if (rewardConfig.chance == 0) return false
        if (rewardConfig.chance == 100) return true

        return rewardConfig.chance > Random.nextInt(0, 101)
    }

}