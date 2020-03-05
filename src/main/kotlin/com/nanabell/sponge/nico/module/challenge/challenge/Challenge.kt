package com.nanabell.sponge.nico.module.challenge.challenge

import com.nanabell.sponge.nico.module.challenge.reward.ChallengeReward
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*

abstract class Challenge(val userId: UUID) {

    abstract val uniqueId: String

    abstract val dependencies: Array<String>


    private var challengeReward: ChallengeReward? = null

    abstract fun isComplete(): Boolean

    fun setReward(challengeReward: ChallengeReward) {
        this.challengeReward = challengeReward
    }

    fun onComplete() {
        challengeReward?.applyReward(Cause.of(EventContext.empty(), this))
    }

}