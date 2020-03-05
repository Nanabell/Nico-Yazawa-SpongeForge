package com.nanabell.sponge.nico.module.challenge.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.challenge.ChallengeModule
import com.nanabell.sponge.nico.module.challenge.challenge.ChallengeStatus
import com.nanabell.sponge.nico.module.challenge.challenge.KillChallenge
import com.nanabell.sponge.nico.module.challenge.data.ChallengeUser
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext

@RegisterService
class ChallengeService : AbstractService<ChallengeModule>() {

    private val users: MutableList<ChallengeUser> = ArrayList()

    override fun onPreEnable() {
    }

    fun onEntityKill(player: Player, living: Living) {
        val user = users.firstOrNull { it.userId == player.uniqueId } ?: return
        user.getActiveChallenges().filterIsInstance(KillChallenge::class.java)
                .forEach { it.onEntityKill(living) }

        tick()
    }


    fun loadPlayer(player: Player) {
        var user = users.firstOrNull { it.userId == player.uniqueId }
        if (user == null) {
            user = ChallengeUser(player.uniqueId)

            users.add(user)
        }

        user.loadChallenges()
    }

    fun savePlayer(player: Player) {
        val user = users.firstOrNull { it.userId == player.uniqueId } ?: return

        users.remove(user)
        user.save()
    }
    // Challenge Requirements to realized Tiered Challenges (kill 50 -> 100 -> 200...)
    // Persist & Load Challenges
    // Implement "Repeatable" Challenges
    // Permissions?

    private fun tick() {
        for (user in users) {
            user.getActiveChallenges().forEach {
                if (it.isComplete()) {
                    it.challengeReward.applyReward(user.userId, Cause.of(EventContext.empty(), this))
                    it.status = ChallengeStatus.CLAIMED
                }
            }
        }
    }

}
