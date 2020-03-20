package com.nanabell.sponge.nico.module.quest.data.reward

import com.nanabell.sponge.nico.module.quest.data.RegistryHolder
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
abstract class Reward(
        override val id: UUID
) : RegistryHolder(), IReward {

    override fun isAttached(): Boolean {
        return questRegistry.getAll().any { it.rewards.contains(this.id) }
    }
}