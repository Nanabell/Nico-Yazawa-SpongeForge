package com.nanabell.sponge.nico.module.challenge.challenge

import com.nanabell.sponge.nico.module.challenge.reward.ChallengeReward
import org.spongepowered.api.entity.living.Living
import java.util.*

class KillGenericChallenge(
        challengeId: String,
        challengeReward: ChallengeReward,
        dependencies: Array<String> = emptyArray(),
        private val amount: Int
) : KillChallenge(challengeId, challengeReward, dependencies) {

    private var kills = 0

    override fun onEntityKill(killed: Living) {
        kills++

        if (kills >= amount && status == ChallengeStatus.ACTIVE)
            status = ChallengeStatus.COMPLETED
    }
}