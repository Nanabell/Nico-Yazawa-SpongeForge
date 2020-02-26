package com.nanabell.sponge.nico.module.afk.data

import com.nanabell.sponge.nico.module.afk.event.PlayerAFKEvent
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import java.time.Instant
import java.util.*

class AfkPlayer(val uuid: UUID) {

    private val eventManager = Sponge.getEventManager()

    var lastInteract: Instant = Instant.now()
        private set

    var isAFK = false
        private set

    var afkSince: Instant = Instant.MAX
        private set

    fun startAFK(player: Player, cause: Cause) {
        if (eventManager.post(PlayerAFKEvent(this, player, cause))) return

        forceStartAFK()
    }

    fun forceStartAFK() {
        afkSince = Instant.now()
        isAFK = true
    }

    fun stopAFK(player: Player, cause: Cause) {
        if (eventManager.post(PlayerActiveEvent(this, player, cause))) return

        forceStopAFK()
    }

    fun forceStopAFK() {
        isAFK = false

        afkSince = Instant.MAX
        lastInteract = Instant.now()
    }

    fun interact(player: Player, cause: Cause) {
        lastInteract = Instant.now()
        if (isAFK) stopAFK(player, cause)
    }
}