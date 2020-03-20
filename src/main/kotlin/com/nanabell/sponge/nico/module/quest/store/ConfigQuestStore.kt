package com.nanabell.sponge.nico.module.quest.store

import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.QuestStore
import com.nanabell.sponge.nico.module.quest.serializer.ConfigSerializerRegistry
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path
import java.util.*

class ConfigQuestStore(path: Path) : QuestStore {

    private val loader = HoconConfigurationLoader.builder().setPath(path).build()
    private val node = loader.load()

    override fun load(questId: UUID): IQuest? {
        val questNode = node.getNode(questId.toString())
        val type = questNode.getNode("type").string ?: return null

        return ConfigSerializerRegistry.getQuestSerializer(type).deserialize(questNode, questId)
    }

    override fun loadAll(): List<IQuest> {
        return node.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull { load(it) }
    }

    override fun save(quest: IQuest) {
        val questNode = node.getNode(quest.id.toString())
        val root = ConfigSerializerRegistry.getQuestSerializer(quest.type).serialize(quest)

        root.getNode("type").value = quest.type
        questNode.value = root

        loader.save(node)
    }

    override fun saveAll(quests: List<IQuest>) {
        quests.forEach { save(it) }
    }

    override fun remove(quest: IQuest) {
        node.removeChild(quest.id.toString())
        loader.save(node)
    }

    override fun removeAll() {
        node.childrenMap.keys.forEach { node.removeChild(it) }
        loader.save(node)
    }
}