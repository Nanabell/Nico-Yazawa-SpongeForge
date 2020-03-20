package com.nanabell.sponge.nico.module.quest.store

import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.TaskStore
import com.nanabell.sponge.nico.module.quest.serializer.ConfigSerializerRegistry
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path
import java.util.*

class ConfigTaskStore(path: Path) : TaskStore {

    private val loader = HoconConfigurationLoader.builder().setPath(path).build()
    private val node = loader.load()


    override fun load(taskId: UUID): ITask? {
        val taskNode = node.getNode(taskId.toString())
        val type = taskNode.getNode("type").string ?: return null

        return ConfigSerializerRegistry.getTaskSerializer(type).deserializeTask(taskNode, taskId)
    }

    override fun loadAll(): List<ITask> {
        return node.childrenMap.keys.map { UUID.fromString(it as String) }.mapNotNull { load(it) }
    }

    override fun save(task: ITask) {
        val taskNode = node.getNode(task.id.toString())
        val root = ConfigSerializerRegistry.getTaskSerializer(task.type).serializeTask(task)

        root.getNode("type").value = task.type
        taskNode.value = root

        loader.save(node)
    }

    override fun saveAll(tasks: List<ITask>) {
        tasks.forEach { save(it) }
    }

    override fun remove(task: ITask) {
        node.removeChild(task.id.toString())
        loader.save(node)
    }

    override fun removeAll() {
        node.childrenMap.forEach { node.removeChild(it) }
        loader.save(node)
    }
}