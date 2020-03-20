package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.module.quest.data.RegistryHolder
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import java.util.*

abstract class Task(
        override val id: UUID
) : RegistryHolder(), ITask {

    override fun getProgress(userId: UUID): ITaskProgress {
        val user = userRegistry.get(userId)
        var progress = user.getTaskProgress(this.id)
        if (progress == null) {
            progress = newProgress()

            user.setTaskProgress(this.id, progress)
            userRegistry.set(user)
        }

        return progress
    }

    override fun isAttached(): Boolean {
        return questRegistry.getAll().any { it.tasks.contains(this.id) }
    }
}