package com.nanabell.sponge.nico.activity

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.config.PaymentConfig
import com.nanabell.sponge.nico.economy.currency.NicoCurrency
import com.nanabell.sponge.nico.extensions.gold
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.extensions.yellow
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
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
            val config = configManager.get()

            logger.debug("Running main activity task")
            for ((_, player) in activityPlayers) {
                val mcPlayer = Sponge.getServer().getPlayer(player.uuid).orNull() ?: continue

                // Set inactive players afk
                if (!player.isAFK) {
                    val inactiveSince = (System.currentTimeMillis() - player.lastInteract) / 1000

                    val afkTimeout = config.activityConfig.afkTimeout
                    if (afkTimeout < 1) {
                        logger.debug("Skipping AFK detection as afkTimeout is less than 1")
                        continue
                    }

                    if (inactiveSince >= afkTimeout) {
                        if (mcPlayer.hasPermission("nico.activity.afk-immunity")){
                            logger.debug("Skip setting AFK on user ${mcPlayer.name} because of 'nico.activity.afk-immunity' permission")
                            continue
                        }


                        player.startAFK(Cause.of(EventContext.of(mapOf(NicoConstants.INACTIVE to inactiveSince)), this))
                        logger.info("Changed ${mcPlayer.name}'s status to AFK")
                    }
                }

                // Handle Nico Points
                val cooldownTimer = config.activityConfig.cooldownTimer
                if (cooldownTimer > 0) {
                    val elapsedCooldown = (System.currentTimeMillis() - player.lastCooldown) / 1000

                    if (cooldownTimer > elapsedCooldown) {
                        logger.debug("Skipping player ${mcPlayer.name}. Still on cooldown for ${cooldownTimer - elapsedCooldown} seconds")
                        continue
                    }
                }

                if (player.isAFK) {
                    logger.debug("Skipping player ${mcPlayer.name}. Player is AFK")
                    continue // no points for AFKs ヽ༼ ಠ益ಠ ༽ﾉ
                }



                if (configManager.get().activityConfig.disabledWorlds.contains(mcPlayer.world.name)) {
                    logger.debug("Skipping player ${mcPlayer.name}. Player is in disabled world ${mcPlayer.world.name}")
                    continue // Your Nico points are in another Castle (World)
                }


                payout@ for (paymentConfig in configManager.get().activityConfig.paymentConfigs) {
                    if (checkPayout(paymentConfig, mcPlayer, player)) continue

                    val account = economy.getOrCreateAccount(player.uuid).orNull()
                    val currency = NicoCurrency.instance

                    if (account != null && account.hasBalance(currency)) {
                        player.lastCooldown = System.currentTimeMillis()

                        val amount = BigDecimal(paymentConfig.paymentAmount)
                        val cause = Cause.of(EventContext.of(mapOf(NicoConstants.ACTIVITY_PLAYER to player)), this)
                        account.deposit(currency, amount, cause)

                        mcPlayer.sendMessage(ChatTypes.ACTION_BAR, "You have earned ".toText().gold()
                                .concat(currency.format(amount).yellow())
                                .concat(" for being active".toText().gold()))

                        logger.info("Awarded ${mcPlayer.name} with $amount ${currency.pluralDisplayName.toPlain()}")
                    } else {
                        logger.warn("Unable to award ${mcPlayer.name} with ${paymentConfig.paymentAmount} due to missing EconomyAccount")
                    }

                    break@payout
                }
            }
        }
    }

    private fun checkPayout(paymentConfig: PaymentConfig, player: Player, activityPlayer: ActivityPlayer): Boolean {
        if (paymentConfig.requiredPermission.isNotEmpty() && !player.hasPermission(paymentConfig.requiredPermission)) {
            logger.debug("Skipping payment for ${player.name}. Player does not have the required Permission ${paymentConfig.requiredPermission}")
            return false
        }

        if (paymentConfig.dailyPaymentLimit > 0 && activityPlayer.totalPayment >= paymentConfig.dailyPaymentLimit) {
            logger.debug("Skipping payment for ${player.name}. Player reached daily payout limit ${paymentConfig.dailyPaymentLimit}")
            return false
        }

        if (paymentConfig.paymentChance == 0) {
            logger.debug("Skipping payment for ${player.name}. Payout chance is 0")
            return false
        }

        if (paymentConfig.paymentChance == 100) {
            return true
        }

        val rnd = RANDOM.nextInt(101)
        if (rnd > paymentConfig.paymentChance) {
            logger.debug("Skipping payment for ${player.name}. Random $rnd > ${paymentConfig.paymentChance}")
            return false
        }

        return true
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