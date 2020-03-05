package com.nanabell.sponge.nico.module.challenge.data

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.module.challenge.challenge.Challenge
import com.nanabell.sponge.nico.module.challenge.service.ChallengeRegistry
import java.util.*

class ChallengeUser(val userId: UUID) {

    private val challenges: MutableList<Challenge> = mutableListOf()

    fun getActiveChallenges(): List<Challenge> {
        return challenges.filter { it.isActive() }
    }

    fun loadChallenges(): List<Challenge> {
        return NicoYazawa.getServiceRegistry().provideUnchecked<ChallengeRegistry>().getDefaults()
        // TODO: Implement Persistence
    }

    fun save() {

    }

    fun addChallenge(challenge: Challenge) {
        challenges.removeIf { it.challengeId == challenge.challengeId }
        challenges.add(challenge)
    }

    fun removeChallenge(id: String) {
        challenges.removeIf { it.challengeId == id }
    }

}