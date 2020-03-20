package com.nanabell.sponge.nico.module.quest.interfaces.quest

import java.util.*

interface QuestStore {

    fun load(questId: UUID): IQuest?
    fun loadAll(): List<IQuest>

    fun save(quest: IQuest)
    fun saveAll(quests: List<IQuest>)

    fun remove(quest: IQuest)
    fun removeAll()

}