package com.nanabell.sponge.nico.module.quest.serializer

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.RewardConfigSerializer
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import java.util.*
import kotlin.reflect.KClass

@Suppress("UnstableApiUsage", "UNCHECKED_CAST")
class DefaultRewardSerializer<T : IReward>(clazz: KClass<T>) : RewardConfigSerializer {

    private val token: TypeToken<T> = TypeToken.of(clazz.java)

    override fun serialize(reward: IReward): ConfigurationNode {
        return SimpleCommentedConfigurationNode.root().setValue(token, reward as T)
    }

    override fun deserialize(node: ConfigurationNode, rewardId: UUID): IReward? {
        return node.getValue(token)?.copy(rewardId)
    }
}