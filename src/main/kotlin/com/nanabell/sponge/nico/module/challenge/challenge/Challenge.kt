package com.nanabell.sponge.nico.module.challenge.challenge

import com.nanabell.sponge.nico.module.challenge.reward.ChallengeReward

abstract class Challenge(
        val challengeId: String,
        val challengeReward: ChallengeReward,
        val dependencies: Array<String>
) {

    var status: ChallengeStatus = ChallengeStatus.ACTIVE

    fun isComplete(): Boolean {
        return status == ChallengeStatus.COMPLETED
    }

    fun isActive(): Boolean {
        return status == ChallengeStatus.ACTIVE
    }

    fun setActive() {
        status = ChallengeStatus.ACTIVE
    }
}