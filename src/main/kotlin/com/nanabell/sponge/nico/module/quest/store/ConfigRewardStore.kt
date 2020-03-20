package com.nanabell.sponge.nico.module.quest.store

import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.RewardStore
import com.nanabell.sponge.nico.module.quest.serializer.ConfigSerializerRegistry
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path
import java.util.*

class ConfigRewardStore(path: Path) : RewardStore {

    private val loader = HoconConfigurationLoader.builder().setPath(path).build()
    private val node = loader.load()

    override fun load(rewardId: UUID): IReward? {
        val rewardNode = node.getNode(rewardId.toString())
        val type = rewardNode.getNode("type").string ?: return null

        return ConfigSerializerRegistry.getRewardSerializer(type).deserialize(rewardNode, rewardId)
    }

    override fun loadAll(): List<IReward> {
        return node.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull { load(it) }
    }

    override fun save(reward: IReward) {
        val rewardNode = node.getNode(reward.id.toString())
        val root = ConfigSerializerRegistry.getRewardSerializer(reward.type).serialize(reward)

        root.getNode("type").value = reward.type
        rewardNode.value = root

        loader.save(node)
    }

    override fun saveAll(rewards: List<IReward>) {
        rewards.forEach { save(it) }
    }

    override fun remove(reward: IReward) {
        node.removeChild(reward.id.toString())
        loader.save(node)
    }

    override fun removeAll() {
        node.childrenMap.keys.forEach { node.removeChild(it) }
        loader.save(node)
    }
}