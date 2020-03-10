package com.nanabell.sponge.nico.module.sync.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterSchedule
import com.nanabell.sponge.nico.internal.extension.formatDefault
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.schedule.AbstractSchedule
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.link.service.LinkService
import com.nanabell.sponge.nico.module.sync.SyncModule
import org.quartz.*
import org.spongepowered.api.Sponge

@RegisterSchedule
class UserLinkWatchdog : AbstractSchedule<SyncModule>() {

    override fun registerJobs(): List<JobDetail> {
        return listOf(
                JobBuilder.newJob(UserLinkWatchdog::class.java)
                        .withIdentity(UserLinkWatchdog::class.simpleName, SyncModule::class.simpleName)
                        .storeDurably()
                        .build()
        )
    }

    override fun registerTriggers(): List<Trigger> {
        return listOf(
                TriggerBuilder.newTrigger()
                        .withIdentity("${UserLinkWatchdog::class.simpleName}Trigger", SyncModule::class.simpleName)
                        .forJob(UserLinkWatchdog::class.simpleName, SyncModule::class.simpleName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(5)
                                .repeatForever())
                        .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.MINUTE))
                        .build()
        )
    }

    override fun execute(context: JobExecutionContext) {
        val config = (context.mergedJobDataMap["module"] as SyncModule).getConfigOrDefault().kickConfig
        val playtimeService: PlaytimeService = NicoYazawa.getServiceRegistry().provideUnchecked()
        val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

        if (!config.enabled) return // TODO: Pause Job if disabled

        for (player in Sponge.getServer().onlinePlayers) {
            if (linkService.isLinked(player)) { // TODO: Possibly in the future retrieve all links at once
                continue
            }

            val duration = playtimeService.getSessionPlayTime(player)
            if (duration.seconds >= config.kickPlaytime) {
                player.kick(config.information.replace("{playtime}", duration.formatDefault()).toText())
                logger.info("${player.name} has been kicked after being on the server for ${duration.formatDefault()} and not having their Account linked to Discord")
            }
        }
        logger.debug("Finished UserLinkWatchdog")
    }
}