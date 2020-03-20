package com.nanabell.sponge.nico.module.quest.data.user

import com.nanabell.sponge.nico.module.quest.data.RegistryHolder
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.user.IUser
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
class User(
        override val id: UUID,

        @Setting("quest-progress")
        override val quests: MutableList<UUID>,

        @Setting("task-progress")
        override val tasks: MutableList<ITaskProgress>

) : RegistryHolder(), IUser {

    constructor(id: UUID) : this(id, mutableListOf(), mutableListOf())

    override fun hasCompleted(questId: UUID): Boolean {
        return quests.contains(questId)
    }

    override fun setCompleted(questId: UUID) {
        if (!quests.contains(questId))
            quests.add(questId)
    }

    override fun getActiveQuests(): List<IQuest> {
        return questRegistry.getAll().filter { it.isActive(this.id) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getTaskProgress(taskId: UUID): ITaskProgress? {
        return tasks.firstOrNull { it.id == taskId }
    }

    override fun setTaskProgress(taskId: UUID, progress: ITaskProgress) {
        tasks.removeIf { it.id == taskId }
        tasks.add(progress)
    }

    override fun getProgresses(): List<ITaskProgress> {
        return tasks
    }

    override fun reset(questId: UUID) {
        quests.removeIf { it == questId }

        val toReset = questRegistry.get(questId).tasks.intersect(tasks.map { it.id })
        tasks.removeIf { toReset.contains(it.id) }
    }

    override fun copy(id: UUID): IUser {
        return User(id, quests, tasks)
    }
}