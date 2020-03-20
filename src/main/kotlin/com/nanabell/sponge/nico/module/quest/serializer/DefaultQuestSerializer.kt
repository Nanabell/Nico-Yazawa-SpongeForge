package com.nanabell.sponge.nico.module.quest.serializer

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.QuestConfigSerializer
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import java.util.*
import kotlin.reflect.KClass

@Suppress("UnstableApiUsage", "UNCHECKED_CAST")
class DefaultQuestSerializer<T : IQuest>(clazz: KClass<T>) : QuestConfigSerializer {

    private val token: TypeToken<T> = TypeToken.of(clazz.java)

    override fun serialize(quest: IQuest): ConfigurationNode {
        return SimpleCommentedConfigurationNode.root().setValue(token, quest as T)
    }

    override fun deserialize(node: ConfigurationNode, questId: UUID): IQuest? {
        return node.getValue(token)?.copy(questId)
    }
}