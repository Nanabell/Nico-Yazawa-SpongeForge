package com.nanabell.sponge.nico.module.quest.serializer

import com.nanabell.sponge.nico.module.quest.data.quest.DailyQuest
import com.nanabell.sponge.nico.module.quest.data.quest.SimpleQuest
import com.nanabell.sponge.nico.module.quest.data.quest.WeeklyQuest
import com.nanabell.sponge.nico.module.quest.data.reward.MoneyReward
import com.nanabell.sponge.nico.module.quest.data.task.KillProgress
import com.nanabell.sponge.nico.module.quest.data.task.KillTask
import com.nanabell.sponge.nico.module.quest.data.task.LinkDiscordProgress
import com.nanabell.sponge.nico.module.quest.data.task.LinkDiscordTask
import com.nanabell.sponge.nico.module.quest.interfaces.quest.QuestConfigSerializer
import com.nanabell.sponge.nico.module.quest.interfaces.reward.RewardConfigSerializer
import com.nanabell.sponge.nico.module.quest.interfaces.task.TaskConfigSerializer

object ConfigSerializerRegistry {

    private val questSerializers: Map<String, QuestConfigSerializer> = mapOf(
            "Quest" to DefaultQuestSerializer(SimpleQuest::class),
            "Daily Quest" to DefaultQuestSerializer(DailyQuest::class),
            "Weekly Quest" to DefaultQuestSerializer(WeeklyQuest::class)
    )

    private val taskSerializers: Map<String, TaskConfigSerializer> = mapOf(
            "KillTask" to DefaultTaskSerializer(KillTask::class, KillProgress::class),
            "DiscordLinkTask" to DefaultTaskSerializer(LinkDiscordTask::class, LinkDiscordProgress::class)
    )

    private val rewardSerializers: Map<String, RewardConfigSerializer> = mapOf(
            "MoneyReward" to DefaultRewardSerializer(MoneyReward::class)
    )

    fun getQuestSerializer(type: String): QuestConfigSerializer {
        return questSerializers[type] ?: throw IllegalArgumentException("There is no Serializer registered for type $type!")
    }

    fun getTaskSerializer(type: String): TaskConfigSerializer {
        return taskSerializers[type] ?: throw IllegalArgumentException("There is no Serializer registered for type $type!")
    }

    fun getRewardSerializer(type: String): RewardConfigSerializer {
        return rewardSerializers[type] ?: throw IllegalArgumentException("There is no Serializer registered for type $type!")
    }

}