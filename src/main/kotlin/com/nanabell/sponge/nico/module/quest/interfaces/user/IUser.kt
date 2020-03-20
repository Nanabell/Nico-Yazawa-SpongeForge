package com.nanabell.sponge.nico.module.quest.interfaces.user

import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import java.util.*

interface IUser {

    val id: UUID
    val quests: MutableList<UUID>
    val tasks: MutableList<ITaskProgress>

    fun hasCompleted(questId: UUID): Boolean
    fun setCompleted(questId: UUID)
    fun getActiveQuests(): List<IQuest>

    fun getTaskProgress(taskId: UUID): ITaskProgress?
    fun setTaskProgress(taskId: UUID, progress: ITaskProgress)
    fun getProgresses(): List<ITaskProgress>

    fun reset(questId: UUID)

    fun copy(id: UUID): IUser
}