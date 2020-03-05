package com.nanabell.sponge.nico.module.challenge.challenge

import org.spongepowered.api.entity.living.Living
import java.util.*

class KillGenericChallenge(
        userId: UUID,
        override val uniqueId: String,
        override val dependencies: Array<String>,
        private val amount: Int
) : KillChallenge(userId) {

    private var kills = 0

    override fun onEntityKill(killed: Living) {
        kills++
    }

    override fun isComplete(): Boolean {
        return kills >= amount
    }
}