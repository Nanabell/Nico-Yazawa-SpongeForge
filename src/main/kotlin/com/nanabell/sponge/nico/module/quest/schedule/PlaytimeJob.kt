package com.nanabell.sponge.nico.module.quest.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.PlaytimeTask
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
import org.quartz.*
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext

class PlaytimeJob : AbstractSchedule<QuestModule>() {

    override fun registerJobs(): List<JobDetail> {
        return listOf(
                JobBuilder.newJob(PlaytimeJob::class.java)
                        .withIdentity(PlaytimeJob::class.simpleName, QuestModule::class.simpleName)
                        .storeDurably()
                        .build()
        )
    }

    override fun registerTriggers(): List<Trigger> {
        return listOf(
                TriggerBuilder.newTrigger()
                        .withIdentity(PlaytimeJob::class.simpleName + "Trigger", QuestModule::class.simpleName)
                        .forJob(PlaytimeJob::class.simpleName, QuestModule::class.simpleName)
                        .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
                        .startNow()
                        .build()
        )
    }

    override fun execute(p0: JobExecutionContext?) {
        val playtimeService: PlaytimeService = NicoYazawa.getServiceRegistry().provide() ?: return
        val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

        Sponge.getServer().onlinePlayers.forEach { player ->
            val questUser = userRegistry.get(player.uniqueId)
            questUser.getActiveQuests().forEach { quest ->
                val tasks = quest.tasks().filterIsInstance(PlaytimeTask::class.java)
                if (tasks.any() && tasks.all { playtimeService.getSessionPlayTime(player) >= it.duration }) {
                    quest.rewards().forEach { it.reward(questUser.id, Cause.of(EventContext.empty(), this).with(quest, plugin)) }
                    questUser.setCompleted(quest.id)
                }
            }
        }
    }
}