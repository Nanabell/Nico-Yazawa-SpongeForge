package com.nanabell.sponge.nico.module.quest.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterSchedule
import com.nanabell.sponge.nico.internal.extension.broadcast
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.quest.WeeklyQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
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
        val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
        val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

        val quests = questRegistry.getAll().filterIsInstance<WeeklyQuest>()
        for (user in userRegistry.getAll()) {
            quests.forEach { user.reset(it.id) }
            userRegistry.set(user)
        }

        "Weekly Quests have been Reset!".green().broadcast()
    }
}