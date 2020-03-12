package com.nanabell.sponge.nico.module.quest.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterSchedule
import com.nanabell.sponge.nico.internal.extension.broadcast
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.quest.DailyQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry_OLD
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import org.quartz.*

@RegisterSchedule
class DailyQuestResetJob : AbstractSchedule<QuestModule>() {

    override fun registerJobs(): List<JobDetail> {
        return listOf(
                JobBuilder.newJob(DailyQuestResetJob::class.java)
                        .withIdentity(DailyQuestResetJob::class.simpleName, QuestModule::class.simpleName)
                        .storeDurably()
                        .build()
        )
    }

    override fun registerTriggers(): List<Trigger> {
        return listOf(
                TriggerBuilder.newTrigger()
                        .withIdentity(DailyQuestResetJob::class.simpleName + "Trigger" , QuestModule::class.simpleName)
                        .forJob(DailyQuestResetJob::class.simpleName, QuestModule::class.simpleName)
                        .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                                .withIntervalInDays(1))
                        .startAt(DateBuilder.tomorrowAt(0, 0, 0))
                        .build()
        )
    }

    override fun execute(context: JobExecutionContext) {
        val registry: QuestRegistry_OLD = NicoYazawa.getServiceRegistry().provideUnchecked()
        val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()

        tracker.getAll().forEach { (uniqueId, quests) ->
            quests.filterIsInstance(DailyQuest::class.java).forEach { it.reset() }
            registry.save(uniqueId, quests)
        }

        "Daily Quests have been Reset!".green().broadcast()
    }

}
