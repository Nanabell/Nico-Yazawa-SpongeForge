package com.nanabell.sponge.nico.module.quest.store

import com.nanabell.sponge.nico.module.quest.data.user.User
import com.nanabell.sponge.nico.module.quest.interfaces.user.IUser
import com.nanabell.sponge.nico.module.quest.interfaces.user.UserStore
import com.nanabell.sponge.nico.module.quest.serializer.ConfigSerializerRegistry
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path
import java.util.*

class ConfigUserStore(path: Path) : UserStore {

    private val loader = HoconConfigurationLoader.builder().setPath(path).build()
    private val node = loader.load()

    override fun load(userId: UUID): IUser? {
        val userNode = node.getNode(userId.toString())
        if (userNode.isVirtual) return null

        val questsNode = userNode.getNode("quests")
        val quests = questsNode.childrenList.mapNotNull { UUID.fromString(it.string as String) }

        val tasksNode = userNode.getNode("tasks")
        val tasks = tasksNode.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull {
            val taskNode = tasksNode.getNode(it.toString())
            val type = taskNode.getNode("type").string ?: return null

            ConfigSerializerRegistry.getTaskSerializer(type).deserializeProgress(taskNode, it)
        }

        return User(userId, quests.toMutableList(), tasks.toMutableList())
    }

    override fun loadAll(): List<IUser> {
        return node.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull { load(it) }
    }

    override fun save(user: IUser) {
        val userNode = node.getNode(user.id.toString())

        val questsNode = userNode.getNode("quests")
        questsNode.value = user.quests.map { it.toString() }

        val tasksNode = userNode.getNode("tasks")
        tasksNode.childrenMap.keys.forEach { tasksNode.removeChild(it) }

        user.tasks.forEach {
            val taskNode = tasksNode.getNode(it.id.toString())
            val root = ConfigSerializerRegistry.getTaskSerializer(it.type).serializeProgress(it)

            root.getNode("type").value = it.type
            taskNode.value = root
        }

        loader.save(node)
    }

    override fun saveAll(users: List<IUser>) {
        users.forEach { save(it) }
    }

    override fun remove(user: IUser) {
        node.removeChild(user.id.toString())
        loader.save(node)
    }

    override fun removeAll() {
        node.childrenMap.forEach { node.removeChild(it) }
        loader.save(node)
    }

}