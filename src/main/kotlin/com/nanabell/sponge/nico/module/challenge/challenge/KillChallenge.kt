package com.nanabell.sponge.nico.module.challenge.challenge

import org.spongepowered.api.entity.living.Living
import java.util.*

abstract class KillChallenge(userId: UUID) : Challenge(userId) {

    abstract fun onEntityKill(killed: Living)

}


