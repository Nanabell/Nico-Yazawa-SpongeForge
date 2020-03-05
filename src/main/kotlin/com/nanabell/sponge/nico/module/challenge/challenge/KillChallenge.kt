package com.nanabell.sponge.nico.module.challenge.challenge

import com.nanabell.sponge.nico.module.challenge.reward.ChallengeReward
import org.spongepowered.api.entity.living.Living
import java.util.*

abstract class KillChallenge(
        challengeId: String,
        challengeReward: ChallengeReward,
        dependencies: Array<String>
) : Challenge(challengeId, challengeReward, dependencies) {

    abstract fun onEntityKill(killed: Living)

}


