package com.nanabell.sponge.nico.internal.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger

abstract class AbstractListener<M : StandardModule<*>> {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Listener", javaClass.simpleName)

    protected lateinit var module: M
        private set

    open fun onReady() {}

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule<*>) {
        this.module = module as M
    }
}