package com.nanabell.sponge.nico.module.quest.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.reward.InvalidReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.RewardStore
import com.nanabell.sponge.nico.module.quest.store.ConfigRewardStore
import org.spongepowered.api.Sponge
import java.util.*

@RegisterService
class RewardRegistry : AbstractService<QuestModule>() {

    private val cache = Caffeine.newBuilder().build<UUID, IReward> { store.load(it) }
    private lateinit var store: RewardStore

    override fun onPreEnable() {
        val path = Sponge.getConfigManager().getPluginConfig(plugin).directory.resolve("rewards.conf")

        store = ConfigRewardStore(path)
        reload()
    }

    fun has(reward: IReward) = has(reward.id)
    fun has(rewardId: UUID) = cache[rewardId] != null
    fun get(rewardId: UUID) = cache[rewardId] ?: InvalidReward(rewardId)
    fun getAll() = cache.asMap().values
    fun set(reward: IReward) = store.save(reward).also { cache.put(reward.id, reward) }
    fun remove(reward: IReward) = store.remove(reward).also { cache.invalidate(reward.id) }

    fun reload() {
        cache.invalidateAll()
        store.loadAll().forEach { cache.put(it.id, it) }
    }

}
