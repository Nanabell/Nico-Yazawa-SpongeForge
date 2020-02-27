package com.nanabell.sponge.nico.internal.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.interfaces.Reloadable
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

abstract class AbstractRunnable<M : ConfigurableModule<*, *>> : Runnable, Reloadable {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Runnable", javaClass.simpleName)

    protected lateinit var module: M
        private set

    /**
     * Allows to override the set Delay by the @RegisterRunnable.
     *
     * @return a Pair between long and the TimeUnit used or null to not do anything
     */
    open fun overrideDelay(): Pair<Long, TimeUnit>? {
        return null
    }

    /**
     * Allows to override the set Interval by the @RegisterRunnable.
     *
     * @return a Pair between long and the TimeUnit used or null to not do anything
     */
    open fun overrideInterval(): Pair<Long, TimeUnit>? {
        return null
    }


    /**
     * Called immediately before submitting the TaskBuilder for execution.
     * Use this method to do final Initialisations before the runnable is executed
     */
    open fun onReady() {
    }

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule) {
        this.module = module as M
    }


}