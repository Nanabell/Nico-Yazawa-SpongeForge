package com.nanabell.sponge.nico.module.quest.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.InvalidTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.TaskStore
import com.nanabell.sponge.nico.module.quest.store.ConfigTaskStore
import org.spongepowered.api.Sponge
import java.util.*

@RegisterService
class TaskRegistry : AbstractService<QuestModule>() {

    private val cache = Caffeine.newBuilder().build<UUID, ITask> { store.load(it) }
    private lateinit var store: TaskStore

    override fun onPreEnable() {
        val path = Sponge.getConfigManager().getPluginConfig(plugin).directory.resolve("tasks.conf")

        store = ConfigTaskStore(path)
        store.loadAll().forEach { cache.put(it.id, it) }
    }

    fun has(task: ITask) = has(task.id)
    fun has(taskId: UUID) = cache[taskId] != null
    fun get(taskId: UUID) = cache[taskId] ?: InvalidTask(taskId)
    fun getAll() = cache.asMap().values
    fun set(task: ITask) = store.save(task).also { cache.put(task.id, task) }
    fun remove(task: ITask) = store.remove(task).also { cache.invalidate(task.id) }

}