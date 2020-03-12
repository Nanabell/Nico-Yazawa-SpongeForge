package com.nanabell.sponge.nico.module.quest.interfaces

import java.util.*

interface QuestStore {

    fun load(questId: UUID): IQuest<*>?
    fun loadAll(): List<IQuest<*>>

    fun <T : IQuest<T>> save(quest: IQuest<T>)
    fun saveAll(quests: List<IQuest<*>>)

    fun remove(quest: IQuest<*>)
    fun removeAll()

}