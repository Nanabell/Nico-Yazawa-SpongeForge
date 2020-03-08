package com.nanabell.sponge.nico.internal.schedule

import com.nanabell.sponge.nico.internal.module.StandardModule
import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.Trigger

abstract class AbstractSchedule<M : StandardModule<*>> : Job {

    abstract fun registerJobs(): List<JobDetail>

    abstract fun registerTriggers(): List<Trigger>
}