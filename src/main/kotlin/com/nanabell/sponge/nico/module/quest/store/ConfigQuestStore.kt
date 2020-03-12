package com.nanabell.sponge.nico.module.quest.store

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.quest.interfaces.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.QuestStore
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path
import java.util.*

class ConfigQuestStore(config: Path) : QuestStore {

    private val loader = HoconConfigurationLoader.builder().setPath(config).build()
    private val node = loader.load()

    override fun load(questId: UUID): IQuest<*>? {
        val questNode = node.getNode(questId.toString())
        val type = Class.forName(questNode.getNode("type").string ?: return null)

        return questNode.getValue(TypeToken.of(type)) as IQuest<*>
    }

    override fun loadAll(): List<IQuest<*>> {
        return node.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull { load(it) }
    }

    override fun <T : IQuest<T>> save(quest: IQuest<T>) {
        val questNode = node.getNode(quest.id.toString())
        questNode.setValue(quest.token, quest)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(quests: List<IQuest<*>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(quest: IQuest<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}