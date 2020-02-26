package com.nanabell.sponge.nico.internal.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger
import org.spongepowered.api.scheduler.Task

abstract class AbstractRunnable<M : ConfigurableModule<*, *>> : Runnable {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Runnable", javaClass.simpleName)

    protected lateinit var module: M
        private set

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule) {
        this.module = module as M
    }

    /**
     * allows for manual overriding any settings set by the annotation.
     *
     * **This method should NOT schedule the Task!**
     */
    open fun builderOverride(builder: Task.Builder) {
    }
}