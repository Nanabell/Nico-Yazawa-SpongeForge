package com.nanabell.sponge.nico.module.quest.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.interfaces.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.QuestStore
import java.util.*
import kotlin.collections.ArrayList

@RegisterService
class QuestRegistry : AbstractService<QuestModule>() {

    private val quests: MutableList<IQuest> = ArrayList()
    private lateinit var store: QuestStore // TODO: Add

    override fun onPreEnable() {
        load()
    }

    fun has(quest: IQuest): Boolean = has(quest.id)
    fun has(questId: UUID): Boolean = quests.any { it.id == questId }

    fun get(questId: UUID): IQuest? {
        val quest = quests.firstOrNull { it.id == questId }
        if (quest == null) {
            quests.add(store.load(questId) ?: return null)
            return get(questId)
        }

        return quest
    }

    fun add(quest: IQuest) {
        if (has(quest)) throw IllegalStateException("Quest already added!")

        quests.add(quest).also { store.save(quest) }
    }

    fun remove(quest: IQuest) {
        if (!has(quest)) throw IllegalStateException("Quest is not in Registry!")

        quests.removeIf { it.id == quest.id }.also { store.remove(quest) }
    }


    fun load() {
        quests.clear().also { quests.addAll(store.loadAll()) }
    }

    fun save() {
        store.removeAll().also { store.saveAll(quests) }
    }
}