package com.nanabell.sponge.nico.module.challenge.reward

import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import java.util.*

abstract class ChallengeReward {

    abstract fun applyReward(userId: UUID, cause: Cause)

}
