package com.nanabell.sponge.nico.module.challenge.reward

import org.spongepowered.api.event.cause.Cause
import java.util.*

abstract class ChallengeReward(protected val userId: UUID) {

    abstract fun applyReward(cause: Cause)
}