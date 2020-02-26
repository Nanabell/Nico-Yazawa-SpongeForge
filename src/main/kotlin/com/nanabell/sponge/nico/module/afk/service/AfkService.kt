package com.nanabell.sponge.nico.module.afk.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig
import com.nanabell.sponge.nico.module.afk.data.AfkPlayer
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.EconomyService
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@RegisterService
class AfkService : AbstractService<AfkModule>() {

    private val economyService: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val afkPlayers = ConcurrentHashMap<UUID, AfkPlayer>()

    private lateinit var config: AfkConfig

    override fun onPreEnable() {
        config = module.getConfigOrDefault()
    }

/*    fun init() {

        // Main Activity Tracker Task
        Sponge.getScheduler().createTaskBuilder()
                .name("NicoYazawa-A-ActivityService")
                .async()
                .delay(2, TimeUnit.MINUTES)
                .interval(2, TimeUnit.MINUTES)
                .execute(activityTask())
                .submit(NicoYazawa.getPlugin())


        // reset Daily Max payouts at 00:15:00 every day
        Sponge.getScheduler().createAsyncExecutor(NicoYazawa.getPlugin()).scheduleAtFixedRate(
                dailyReset(),
                calculateInitialDelay(),
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        )
    }*/

    fun interact(player: Player, cause: Cause) {
        getPlayer(player).interact(player, cause)
    }

    fun isAfk(player: Player): Boolean {
        return getPlayer(player).isAFK
    }

    fun startAfk(player: Player, cause: Cause, force: Boolean = false) {
        if (force) getPlayer(player).forceStartAFK()
        else getPlayer(player).startAFK(player, cause)
    }

    fun stopAfk(player: Player, cause: Cause, force: Boolean = false) {
        if (force) getPlayer(player).forceStopAFK()
        else getPlayer(player).stopAFK(player, cause)
    }

    fun getInactiveDuration(player: Player): Duration {
        return Duration.between(Instant.now(), getPlayer(player).lastInteract)
    }

    fun getAfkDuration(player: Player): Duration {
        val duration = Duration.between(Instant.now(), getPlayer(player).afkSince)
        return if (duration < Duration.ZERO) Duration.ZERO else duration
    }

    fun isImmune(player: Player): Boolean {
        return player.hasPermission("nico.afk.exempt")
    }

    private fun getPlayer(player: Player): AfkPlayer {
        return afkPlayers.computeIfAbsent(player.uniqueId) { AfkPlayer(it) }
    }

/*    private fun activityTask(): Runnable {
        return Runnable {
            val config = config.get()

            logger.debug("Running main activity task")
            for ((_, player) in afkPlayers) {
                val mcPlayer = Sponge.getServer().getPlayer(player.uuid).orNull() ?: continue

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



                if (this.config.get().activityConfig.disabledWorlds.contains(mcPlayer.world.name)) {
                    logger.debug("Skipping player ${mcPlayer.name}. Player is in disabled world ${mcPlayer.world.name}")
                    continue // Your Nico points are in another Castle (World)
                }


                payout@ for (paymentConfig in this.config.get().activityConfig.paymentConfigs) {
                    if (checkPayout(paymentConfig, mcPlayer, player)) continue

                    val account = economyService.getOrCreateAccount(player.uuid).orNull()
                    val currency = NicoCurrency.instance

                    if (account != null && account.hasBalance(currency)) {
                        player.lastCooldown = System.currentTimeMillis()

                        var amount = BigDecimal(paymentConfig.paymentAmount)
                        if (mcPlayer.subjectData.hasParent(paymentConfig.premiumGroup))
                            amount = amount.add(BigDecimal(paymentConfig.premiumBonus))

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
    }*/

/*    private fun checkPayout(paymentConfig: PaymentConfig, player: Player, afkPlayer: AfkPlayer): Boolean {
        if (paymentConfig.requiredPermission.isNotEmpty() && !player.hasPermission(paymentConfig.requiredPermission)) {
            logger.debug("Skipping payment for ${player.name}. Player does not have the required Permission ${paymentConfig.requiredPermission}")
            return false
        }

        if (paymentConfig.dailyPaymentLimit > 0 && afkPlayer.totalPayment >= paymentConfig.dailyPaymentLimit) {
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
    }*/

/*    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val next = now.withHour(0).withMinute(15).withSecond(0)
        if (now > next) next.plusDays(1)

        val duration = Duration.between(next, now)
        return duration.seconds
    }

    private fun dailyReset(): Runnable {
        return Runnable {
            for ((_, player) in afkPlayers) {
                player.totalPayment = 0L
            }
            logger.info("Reset all daily totalPayments to 0")
        }
    }*/

/*    companion object {
        val RANDOM = Random()
    }*/

}