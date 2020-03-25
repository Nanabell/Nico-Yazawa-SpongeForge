package com.nanabell.sponge.nico.module.quest.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.quest.InvalidQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.QuestStore
import com.nanabell.sponge.nico.module.quest.store.ConfigQuestStore
import org.spongepowered.api.Sponge
import java.util.*

@RegisterService
class QuestRegistry : AbstractService<QuestModule>() {

    private val cache = Caffeine.newBuilder().build<UUID, IQuest> { store.load(it) }
    private lateinit var store: QuestStore

    override fun onPreEnable() {
        val path = Sponge.getConfigManager().getPluginConfig(plugin).directory.resolve("quests.conf")

        store = ConfigQuestStore(path)
        reload()
    }

    fun has(quest: IQuest) = has(quest.id)
    fun has(questId: UUID) = cache[questId] != null
    fun get(questId: UUID) = cache[questId] ?: InvalidQuest(questId)
    fun getAll() = cache.asMap().values
    fun set(quest: IQuest) = store.save(quest).also { cache.put(quest.id, quest) }
    fun remove(quest: IQuest) = store.remove(quest).also { cache.invalidate(quest.id) }

    fun reload() {
        cache.invalidateAll()
        store.loadAll().forEach { cache.put(it.id, it) }
    }

}
