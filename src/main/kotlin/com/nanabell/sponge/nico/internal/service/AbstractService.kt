package com.nanabell.sponge.nico.internal.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger

abstract class AbstractService<M : StandardModule<*>> {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Service", javaClass.simpleName)

    protected lateinit var module: M
        private set

    abstract fun onPreEnable()
    open fun onEnable() {}

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule<*>) {
        this.module = module as M
    }

}