package com.nanabell.sponge.nico.module.afk.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig
import com.nanabell.sponge.nico.module.afk.data.AfkPlayer
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@RegisterService
class AfkService : AbstractService<AfkModule>() {

    private val afkPlayers = ConcurrentHashMap<UUID, AfkPlayer>()

    private lateinit var config: AfkConfig

    override fun onPreEnable() {
        config = module.getConfigOrDefault()
    }

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
        return Duration.between(getPlayer(player).lastInteract, Instant.now())
    }

    /**
     * Get the duration for how long the player is currently AFK
     *
     * **NOTE** This value is only valid is the player in question is currently AFK.
     * Consuming Services should check with [AfkService.isAfk] is the player is afk
     *
     * @param player Player in question
     * @return Duration since AfkStart Instant
     */
    fun getAfkDuration(player: Player): Duration {
        val duration = Duration.between(getPlayer(player).afkSince, Instant.now())
        return if (duration < Duration.ZERO) Duration.ZERO else duration
    }

    fun isImmune(player: Player): Boolean {
        return player.hasPermission("nico.afk.exempt")
    }

    private fun getPlayer(player: Player): AfkPlayer {
        return afkPlayers.computeIfAbsent(player.uniqueId) { AfkPlayer(it) }
    }
}