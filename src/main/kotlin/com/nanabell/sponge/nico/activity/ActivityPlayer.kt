package com.nanabell.sponge.nico.activity

import com.nanabell.sponge.nico.activity.event.PlayerAFKEvent
import com.nanabell.sponge.nico.activity.event.PlayerActiveEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class ActivityPlayer(val uuid: UUID) {

    private val eventManager = Sponge.getEventManager()

    var lastInteract = System.currentTimeMillis()

    var isAFK = false
    var afkSince = -1L

    var totalPayment = 0L

    fun startAFK(cause: Cause) {
        if (eventManager.post(PlayerAFKEvent(this, cause))) return

        forceStartAFK()
    }

    fun forceStartAFK() {
        afkSince = System.currentTimeMillis()
        isAFK = true
    }

    fun stopAFK(cause: Cause) {
        if (eventManager.post(PlayerActiveEvent(this, cause))) return

        forceStopAFK()
    }

    fun forceStopAFK() {
        isAFK = false
        afkSince = -1L

        lastInteract = System.currentTimeMillis()
    }

    fun interact(cause: Cause) {
        lastInteract = System.currentTimeMillis()
        if (isAFK) stopAFK(cause)
    }
}