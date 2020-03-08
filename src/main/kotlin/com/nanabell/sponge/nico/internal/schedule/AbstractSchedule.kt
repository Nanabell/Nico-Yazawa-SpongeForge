package com.nanabell.sponge.nico.internal.schedule

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.Trigger
import org.slf4j.Logger

abstract class AbstractSchedule<M : StandardModule<*>> : Job {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Schedule", javaClass.simpleName)

    abstract fun registerJobs(): List<JobDetail>

    abstract fun registerTriggers(): List<Trigger>
}