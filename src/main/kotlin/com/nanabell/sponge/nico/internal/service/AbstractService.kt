package com.nanabell.sponge.nico.internal.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger

abstract class AbstractService<M : ConfigurableModule<*>> {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger

    protected lateinit var module: M

    init {
        this.logger = plugin.getLogger("Service", javaClass.simpleName)
    }

    abstract fun onEnable()

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule) {
        this.module = module as M
    }

}