package com.nanabell.sponge.nico.module.quest.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterSchedule
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.PlaytimeTask
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
import org.quartz.*
import org.spongepowered.api.Sponge

@RegisterSchedule
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
        val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()
        val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

        Sponge.getServer().onlinePlayers.forEach { player ->
            val questUser = userRegistry.get(player.uniqueId)
            questUser.getActiveQuests().forEach { quest ->
                if (quest.tasks().filterIsInstance(PlaytimeTask::class.java).any())
                    tracker.commit(player)
            }
        }
    }
}