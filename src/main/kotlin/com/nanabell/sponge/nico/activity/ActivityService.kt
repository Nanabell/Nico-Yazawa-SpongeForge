package com.nanabell.sponge.nico.activity

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.activity.event.ActivityContextKeys
import com.nanabell.sponge.nico.extensions.gold
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.chat.ChatTypes
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class ActivityService(private val plugin: NicoYazawa) {

    private val economy = Sponge.getServiceManager().provideUnchecked(EconomyService::class.java)
    private val activityPlayers = ConcurrentHashMap<UUID, ActivityPlayer>()
    private val configManager = plugin.configManager
    private val logger = NicoYazawa.getLogger()

    private lateinit var listener: ActivityListener

    fun init() {
        listener = ActivityListener(plugin, this)

        // Main Activity Tracker Task
        Sponge.getScheduler().createTaskBuilder()
                .name("NicoYazawa-A-ActivityService")
                .async()
                .delay(2, TimeUnit.MINUTES)
                .interval(2, TimeUnit.MINUTES)
                .execute(activityTask())
                .submit(plugin)


        // reset Daily Max payouts at 00:15:00 every day
        Sponge.getScheduler().createAsyncExecutor(plugin).scheduleAtFixedRate(
                dailyReset(),
                calculateInitialDelay(),
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        )
    }

    fun getPlayer(player: User): ActivityPlayer {
        return activityPlayers.computeIfAbsent(player.uniqueId) { ActivityPlayer(it) }
    }

    private fun activityTask(): Runnable {
        return Runnable {
            logger.debug("Running main activity task")
            for ((_, player) in activityPlayers) {
                val mcPlayer = Sponge.getServer().getPlayer(player.uuid).orNull() ?: return@Runnable

                // Set inactive players afk
                if (!player.isAFK) {
                    val inactiveSince = (System.currentTimeMillis() - player.lastInteract) / 1000

                    val afkTimeout = configManager.get().activityConfig.afkTimeout
                    if (afkTimeout <= 0) return@Runnable

                    if (inactiveSince >= afkTimeout) {
                        if (mcPlayer.hasPermission("nico.activity.afk-immunity"))
                            return@Runnable

                        player.startAFK(Cause.of(EventContext.of(mapOf(ActivityContextKeys.INACTIVE to inactiveSince)), this))
                        logger.info("Changed ${mcPlayer.name}'s status to AFK")
                    }
                }

                // Handle Nico Points
                if (player.isAFK)
                    return@Runnable // no points for AFKs ヽ༼ ಠ益ಠ ༽ﾉ


                if (configManager.get().activityConfig.disabledWorlds.contains(mcPlayer.world.name))
                    return@Runnable // Your Nico points are in another Castle (World)

                for (paymentConfig in configManager.get().activityConfig.paymentConfigs) {
                    if (!mcPlayer.hasPermission(paymentConfig.requiredPermission)) continue
                    if (paymentConfig.dailyPaymentLimit > 0 && player.totalPayment >= paymentConfig.dailyPaymentLimit) continue
                    if (paymentConfig.paymentChance == 0 || (paymentConfig.paymentChance != 100 && RANDOM.nextInt(101) > paymentConfig.paymentChance)) continue

                    val account = economy.getOrCreateAccount(player.uuid).orNull()
                    if (account != null) {
                        val cause = Cause.of(EventContext.of(mapOf(ActivityContextKeys.PLAYER to player)), this)
                        val result = account.deposit(economy.defaultCurrency, BigDecimal(paymentConfig.paymentAmount), cause)

                        mcPlayer.sendMessage(ChatTypes.ACTION_BAR, "You have earned ".toText().gold()
                                .concat(economy.defaultCurrency.format(result.amount))
                                .concat("for being active".toText().gold()))

                        logger.info("Awarded ${mcPlayer.name} with ${result.amount} ${economy.defaultCurrency.pluralDisplayName.toPlain()}")
                    } else {
                        logger.warn("Unable to award ${mcPlayer.name} with ${paymentConfig.paymentAmount} due to missing EconomyAccount")
                    }

                    return@Runnable
                }
            }
        }
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val next = now.withHour(0).withMinute(15).withSecond(0)
        if (now > next) next.plusDays(1)

        val duration = Duration.between(next, now)
        return duration.seconds
    }

    private fun dailyReset(): Runnable {
        return Runnable {
            for ((_, player) in activityPlayers) {
                player.totalPayment = 0L
            }
            logger.info("Reset all daily totalPayments to 0")
        }
    }

    companion object {
        val RANDOM = Random()
    }
}