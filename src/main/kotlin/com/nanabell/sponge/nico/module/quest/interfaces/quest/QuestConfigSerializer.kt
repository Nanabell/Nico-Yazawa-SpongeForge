package com.nanabell.sponge.nico.module.quest.interfaces.quest

import ninja.leaping.configurate.ConfigurationNode
import java.util.*

interface QuestConfigSerializer {

    fun serialize(quest: IQuest): ConfigurationNode

    fun deserialize(node: ConfigurationNode, questId: UUID): IQuest?

}