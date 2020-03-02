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
     * @param player Player in questions
     * @return Duration since AfkStart Instant or Zero if not afk
     */
    fun getAfkDuration(player: Player): Duration {
        if (!isAfk(player)) return Duration.ZERO

        val duration = Duration.between(getPlayer(player).afkSince, Instant.now())
        return if (duration < Duration.ZERO) Duration.ZERO else duration
    }

    fun isImmune(player: Player): Boolean {
        return player.hasPermission(AfkModule.P_AFK_EXEMPT)
    }

    private fun getPlayer(player: Player): AfkPlayer {
        return afkPlayers.computeIfAbsent(player.uniqueId) { AfkPlayer(it) }
    }
}