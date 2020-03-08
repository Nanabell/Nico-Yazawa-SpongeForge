package com.nanabell.sponge.nico.module.quest.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterSchedule
import com.nanabell.sponge.nico.internal.extension.broadcast
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.quest.WeeklyQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import org.quartz.*

@RegisterSchedule
class WeeklyQuestResetJob : AbstractSchedule<QuestModule>() {

    override fun registerJobs(): List<JobDetail> {
        return listOf(
                JobBuilder.newJob(WeeklyQuestResetJob::class.java)
                        .withIdentity(WeeklyQuestResetJob::class.simpleName, QuestModule::class.simpleName)
                        .storeDurably()
                        .build()
        )
    }

    override fun registerTriggers(): List<Trigger> {
        return listOf(
                TriggerBuilder.newTrigger()
                        .withIdentity(WeeklyQuestResetJob::class.simpleName + "Trigger" , QuestModule::class.simpleName)
                        .forJob(WeeklyQuestResetJob::class.simpleName, QuestModule::class.simpleName)
                        .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                                .withIntervalInWeeks(1))
                        .startAt(DateBuilder.tomorrowAt(0, 0, 0))
                        .build()
        )
    }

    override fun execute(context: JobExecutionContext) {
        val registry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
        val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()

        tracker.getAll().forEach { (uniqueId, quests) ->
            quests.filterIsInstance(WeeklyQuest::class.java).forEach { it.reset() }
            registry.save(uniqueId, quests)
        }

        "Weekly Quests have been Reset!".green().broadcast()
    }
}