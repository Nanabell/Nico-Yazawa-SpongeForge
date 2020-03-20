package com.nanabell.sponge.nico.module.quest.interfaces.reward

import java.util.*

interface RewardStore {

    fun load(rewardId: UUID): IReward?
    fun loadAll(): List<IReward>

    fun save(reward: IReward)
    fun saveAll(rewards: List<IReward>)

    fun remove(reward: IReward)
    fun removeAll()
}