package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.internal.extension.darkRed
import com.nanabell.sponge.nico.module.quest.data.user.TaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import org.spongepowered.api.text.Text
import java.util.*

class InvalidTask(override val id: UUID) : ITask {

        override val type = "!INVALID!"

        override fun getProgress(userId: UUID): ITaskProgress = InvalidProgress(this.id)
        override fun newProgress(): ITaskProgress = InvalidProgress(id)
        override fun isAttached(): Boolean = true
        override fun getName(): Text = "!INVALID TASK!".darkRed()
        override fun getMessage(): Text = "!ERR!".darkRed()
        override fun printSettings(): List<Text> = emptyList()
        override fun copy(id: UUID): ITask = this

}

class InvalidProgress(id: UUID) : TaskProgress(id) {

        override val type: String = "!INVALID!"
        override fun isComplete(): Boolean = false
        override fun getText(): Text = "[ERR]".darkRed()

        override fun copy(id: UUID): ITaskProgress = this

}