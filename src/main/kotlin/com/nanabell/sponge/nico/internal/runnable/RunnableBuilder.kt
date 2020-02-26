package com.nanabell.sponge.nico.internal.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.MissingAnnotationException
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.spongepowered.api.Sponge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class RunnableBuilder(
        private val plugin: NicoYazawa,
        private val module: StandardModule
) {

    fun <R : AbstractRunnable<*>> register(clazz: KClass<out R>) {
        val rr = clazz.findAnnotation<RegisterRunnable>()
                ?: throw MissingAnnotationException(clazz, RegisterRunnable::class)

        val runnable = clazz.createInstance()
        runnable.setModule(module)
        runnable.onReady()

        val builder = Sponge.getScheduler().createTaskBuilder()
        builder.name(rr.value)

        if (rr.isAsync)
            builder.async()

        if (rr.delay > 0)
            builder.delay(rr.delay, rr.delayUnit)

        if (rr.interval > 0)
            builder.interval(rr.interval, rr.intervalUnit)

        // allow implementations to dynamically override these settings if necessary
        val overrideDelay = runnable.overrideDelay()
        if (overrideDelay != null)
            builder.delay(overrideDelay.first, overrideDelay.second)

        val overrideInterval = runnable.overrideInterval()
        if (overrideInterval != null)
            builder.delay(overrideInterval.first, overrideInterval.second)

        module.logger.info("Registered Runnable: ${clazz.simpleName}") // TODO: change back to debug
        builder.execute(runnable)
        builder.submit(plugin)
    }
}