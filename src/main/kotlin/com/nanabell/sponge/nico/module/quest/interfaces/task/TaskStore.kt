package com.nanabell.sponge.nico.module.quest.interfaces.task

import java.util.*

interface TaskStore {

    fun load(taskId: UUID): ITask?
    fun loadAll(): List<ITask>

    fun save(task: ITask)
    fun saveAll(tasks: List<ITask>)

    fun remove(task: ITask)
    fun removeAll()

}