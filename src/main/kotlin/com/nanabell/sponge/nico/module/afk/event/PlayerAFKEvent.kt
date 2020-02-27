package com.nanabell.sponge.nico.module.afk.event

import com.nanabell.sponge.nico.internal.event.StandardCancellableEvent
import com.nanabell.sponge.nico.module.afk.data.AfkPlayer
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent
import java.time.Instant

class PlayerAFKEvent(inactiveSince: Instant, private val player: Player, cause: Cause) : StandardCancellableEvent(cause), TargetPlayerEvent {

    override fun getTargetEntity(): Player = player

}
