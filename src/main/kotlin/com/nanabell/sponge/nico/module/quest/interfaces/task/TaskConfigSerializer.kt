package com.nanabell.sponge.nico.module.quest.interfaces.task

import ninja.leaping.configurate.ConfigurationNode
import java.util.*

interface TaskConfigSerializer {

    fun serializeTask(task: ITask): ConfigurationNode

    fun deserializeTask(node: ConfigurationNode, taskId: UUID): ITask?

    fun serializeProgress(task: ITaskProgress): ConfigurationNode

    fun deserializeProgress(node: ConfigurationNode, taskId: UUID): ITaskProgress?

}