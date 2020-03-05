package com.nanabell.sponge.nico.module.challenge.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.challenge.ChallengeModule
import com.nanabell.sponge.nico.module.challenge.challenge.Challenge
import com.nanabell.sponge.nico.module.challenge.challenge.KillChallenge
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.player.Player

@RegisterService
class ChallengeService : AbstractService<ChallengeModule>() {

    private val challenges: MutableList<Challenge> = ArrayList()

    override fun onPreEnable() {

    }

    fun onEntityKill(player: Player, living: Living) {
        challenges.filterIsInstance(KillChallenge::class.java)
                .filter { !it.isComplete() }
                .forEach { it.onEntityKill(living) }
    }

    // Challenge Requirements to realized Tiered Challenges (kill 50 -> 100 -> 200...)
    // Persist & Load Challenges
    // Implement "Repeatable" Challenges
    // Permissions?

}