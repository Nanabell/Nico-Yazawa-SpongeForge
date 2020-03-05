package com.nanabell.sponge.nico.module.challenge.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.challenge.ChallengeModule
import com.nanabell.sponge.nico.module.challenge.challenge.Challenge
import com.nanabell.sponge.nico.module.challenge.challenge.KillGenericChallenge
import com.nanabell.sponge.nico.module.challenge.reward.EconomyChallengeReward

@RegisterService
class ChallengeRegistry : AbstractService<ChallengeModule>() {

    override fun onPreEnable() {
    }

    fun getDefaults(): List<Challenge> {
        return listOf(
                KillGenericChallenge("kill-generic-50", EconomyChallengeReward(50), amount = 50),
                KillGenericChallenge("kill-generic-100", EconomyChallengeReward(100), arrayOf("kill-generic-50"), 100),
                KillGenericChallenge("kill-generic-200", EconomyChallengeReward(200), arrayOf("kill-generic-100"), 200),
                KillGenericChallenge("kill-generic-500", EconomyChallengeReward(500), arrayOf("kill-generic-200"), 500)
        )
    }

}
