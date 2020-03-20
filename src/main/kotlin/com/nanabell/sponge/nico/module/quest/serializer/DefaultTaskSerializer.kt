package com.nanabell.sponge.nico.module.quest.serializer

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.task.TaskConfigSerializer
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import java.util.*
import kotlin.reflect.KClass

@Suppress("UnstableApiUsage", "UNCHECKED_CAST")
class DefaultTaskSerializer<T : ITask, P : ITaskProgress>(clazz: KClass<T>, clazzP : KClass<P>) : TaskConfigSerializer {

    private val token: TypeToken<T> = TypeToken.of(clazz.java)
    private val progressToken: TypeToken<P> = TypeToken.of(clazzP.java)

    override fun serializeTask(task: ITask): ConfigurationNode {
        return SimpleCommentedConfigurationNode.root().setValue(token, task as T)
    }

    override fun deserializeTask(node: ConfigurationNode, taskId: UUID): ITask? {
        return node.getValue(token)?.copy(taskId)
    }

    override fun serializeProgress(task: ITaskProgress): ConfigurationNode {
        return SimpleConfigurationNode.root().setValue(progressToken, task as P)
    }

    override fun deserializeProgress(node: ConfigurationNode, taskId: UUID): ITaskProgress? {
        return node.getValue(progressToken)?.copy(taskId)
    }
}