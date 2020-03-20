package com.nanabell.sponge.nico.module.quest.interfaces.reward

import ninja.leaping.configurate.ConfigurationNode
import java.util.*

interface RewardConfigSerializer {

    fun serialize(reward: IReward): ConfigurationNode

    fun deserialize(node: ConfigurationNode, rewardId: UUID): IReward?

}