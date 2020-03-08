package com.nanabell.sponge.nico.internal.schedule

import com.nanabell.sponge.nico.internal.module.StandardModule
import org.quartz.Scheduler
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ScheduleBuilder(private val scheduler: Scheduler, private val module: StandardModule<*>) {

    fun <S : AbstractSchedule<*>> register(clazz: KClass<out S>) {
        val schedule = clazz.createInstance()

        val jobs = schedule.registerJobs()
        jobs.forEach { it.jobDataMap["module"] = module }
        if (jobs.any { !it.isDurable }) {
            throw IllegalStateException("Module Jobs must be Stored Durable in $clazz")
        }

        jobs.forEach {
            scheduler.addJob(it, false)
        }

        val triggers = schedule.registerTriggers()
        if (triggers.any { it.jobKey == null }) {
            throw IllegalStateException("A Trigger is not assigned to a Job in $clazz")
        }

        triggers.forEach {
            scheduler.scheduleJob(it)
        }
    }
}