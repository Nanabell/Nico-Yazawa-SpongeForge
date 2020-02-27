package com.nanabell.sponge.nico.module.afk.event

import com.nanabell.sponge.nico.internal.event.StandardCancellableEvent
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent
import java.time.Duration
import java.time.Instant

class PlayerActiveEvent(private val afkSince: Instant, private val player: Player, cause: Cause) : StandardCancellableEvent(cause), TargetPlayerEvent {

    override fun getTargetEntity(): Player = player

    fun getAfkDuration(): Duration {
        return Duration.between(afkSince, Instant.now())
    }

}

