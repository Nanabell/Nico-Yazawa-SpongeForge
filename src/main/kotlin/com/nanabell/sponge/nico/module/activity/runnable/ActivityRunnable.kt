package com.nanabell.sponge.nico.module.activity.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import com.nanabell.sponge.nico.module.afk.service.AfkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-A-ActivityService", 2, TimeUnit.MINUTES, 2, TimeUnit.MINUTES, true)
class ActivityRunnable : AbstractRunnable<ActivityModule>() {

    private val activityService: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val economyService: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val afkService: AfkService? = NicoYazawa.getServiceRegistry().provide()

    private lateinit var config: ActivityConfig

    override fun onReady() {
        config = module.getConfigOrDefault()
    }

    override fun run() {
        for (player in Sponge.getServer().onlinePlayers) {

            if (activityService.isOnCooldown(player))
                continue // Ignore players who are on cooldown

            if (afkService?.isAfk(player) == true)
                continue // Player is afk or no AfkService Registered

            if (config.disabledWorlds.contains(player.world.name))
                continue // Player is in a disabled world

            for (reward in config.rewards) {
                if (!activityService.checkRewardContext(reward, player))
                    continue

                val currency = economyService.currencies.firstOrNull { it.name == reward.currency } ?: continue
                val account = economyService.getOrCreateAccount(player.uniqueId).orNull() ?: continue

                if (!account.hasBalance(currency))
                    continue

                var amount = BigDecimal(reward.amount)
                if (player.hasPermission(reward.bonusPermission))
                    amount = amount.plus(BigDecimal(reward.bonusAmount))

                val result = account.deposit(currency, amount, Cause.of(EventContext.empty(), this))
                if (result.result != ResultType.SUCCESS) {

                    continue
                }

                break
            }
        }
    }

}