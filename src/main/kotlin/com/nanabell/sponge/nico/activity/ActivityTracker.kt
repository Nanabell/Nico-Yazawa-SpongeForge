package com.nanabell.sponge.nico.activity

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.config.ActivityConfig
import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.service.ServiceManager
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.chat.ChatTypes
import java.math.BigDecimal
import java.time.Duration
import java.util.*

class ActivityTracker(private val plugin: NicoYazawa) : Runnable {

    //TODO: Make Pretty again!!
    private val logger = NicoYazawa.getLogger()
    private val configManager: Config<MainConfig> = plugin.configManager
    private val serviceManager: ServiceManager = Sponge.getServiceManager()

    private var task: Task? = null
    private var dayOfYear = 0

    private val activityMap: MutableMap<UUID, Int> = HashMap()
    private val paymentMap: MutableMap<UUID, Int> = HashMap()

    fun init() {
        if (task != null) {
            logger.warn("Activity Task is running but init was called. Cancelling old Task")
            cancel()
        }

        task = Sponge.getScheduler().createTaskBuilder()
                .execute(this)
                .delayTicks(20)
                .intervalTicks(20)
                .name("Nicos-Coffe_Activity_Tracker")
                .async()
                .submit(plugin)

        dayOfYear = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        logger.info("Started Activity Tracker Task")
    }

    private fun cancel() {
        try {
            task!!.cancel()
        } finally {
            task = null
        }
    }

    override fun run() {
        val activityConfig = configManager.get().activityConfig
        for (player in Sponge.getServer().onlinePlayers) {
            if (activityConfig.disabledWorlds.contains(player.world.name)) {
                continue
            }
            activityMap[player.uniqueId] = activityMap.computeIfAbsent(player.uniqueId) { 0 } + 1
            if (activityMap[player.uniqueId]!! >= activityConfig.paymentInterval) {
                activityMap.remove(player.uniqueId)
                payout(player, activityConfig)
            }
        }
        val tmp = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        if (dayOfYear != tmp) {
            logger.info("New Day. Resetting daily payment Limits & activity Maps")
            activityMap.clear()
            paymentMap.clear()
            dayOfYear = tmp
        }
    }

    private fun payout(player: Player, activityConfig: ActivityConfig) {
        for (paymentConfig in activityConfig.paymentConfigs) {
            if (paymentConfig.dailyPaymentLimit != 0 && paymentMap.getOrDefault(player.uniqueId, 0) > paymentConfig.dailyPaymentLimit) {
                continue
            }
            if (paymentConfig.paymentChance == 0 || paymentConfig.paymentChance != 100 && RANDOM.nextInt(101) > paymentConfig.paymentChance) {
                continue
            }
            if (!player.hasPermission(paymentConfig.requiredPermission)) {
                continue
            }
            paymentMap[player.uniqueId] = paymentMap.computeIfAbsent(player.uniqueId) { paymentConfig.paymentAmount }
            val economyService = serviceManager.provideUnchecked(EconomyService::class.java)
            val oAccount = economyService.getOrCreateAccount(player.uniqueId)
            if (oAccount.isPresent) {
                val account = oAccount.get()
                account.deposit(economyService.defaultCurrency, BigDecimal(paymentConfig.paymentAmount), Cause.of(EventContext.empty(), this))
                logger.debug("Deposited " + paymentConfig.paymentAmount + " Currency to " + player + "'s Account")
                player.sendMessage(ChatTypes.ACTION_BAR, Text.of("You have earned ")
                        .concat(Text.of(economyService.defaultCurrency.format(BigDecimal(paymentConfig.paymentAmount))))
                        .concat(Text.of(" for playing "))
                        .concat(Text.of(Duration.ofSeconds(activityConfig.paymentInterval.toLong()).toString().substring(2).replace("(\\d[HMS])(?!$)".toRegex(), "$1 ").toLowerCase()))
                        .concat(Text.of(" Online!")))
            }
            return
        }
    }

    companion object {
        private val RANDOM = Random()
    }

}