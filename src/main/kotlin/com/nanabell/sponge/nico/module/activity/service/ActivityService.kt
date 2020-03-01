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
        cooldowns.add(Cooldown(player.uniqueId, player.getOptionDuration(ActivityModule.O_ACTIVITY_COOLDOWN, config.rewardCooldown)))
    }

    /**
     * Pause the cooldown for a given player
     * If the player has no cooldown registered this request is quietly ignored
     *
     * @param player player in question
     */
    fun pauseCooldown(player: Player) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == player.uniqueId } ?: return
        cooldown.pause()
    }

    /**
     * Resume the cooldown for a given player
     * If the player has no cooldown registered this request is quietly ignored
     *
     * @param player player in question
     */
    fun resumeCooldown(player: Player) {
        val cooldown = cooldowns.firstOrNull { it.uniqueId == player.uniqueId } ?: return
        cooldown.resume()
    }

    /**
     * Remove the cooldown for a given player
     *
     * @param player player in question
     * @return [Boolean] if successful
     */
    fun removeCooldown(player: Player): Boolean {
        return cooldowns.removeIf { it.uniqueId == player.uniqueId }
    }

    fun getPayoutAmount(player: Player): BigDecimal {
        return payouts.filter { it.uniqueId == player.uniqueId }.sumByDouble { it.amount.toDouble() }.toBigDecimal()
    }

    fun getPayoutCount(player: Player): Int {
        return payouts.filter { it.uniqueId == player.uniqueId }.count()
    }

    fun addPayout(player: Player, amount: BigDecimal) {
        payouts.add(Payout(player.uniqueId, amount))
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