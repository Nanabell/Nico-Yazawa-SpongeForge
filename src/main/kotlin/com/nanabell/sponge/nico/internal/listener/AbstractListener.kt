package com.nanabell.sponge.nico.internal.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.slf4j.Logger

abstract class AbstractListener<M : ConfigurableModule<*>> {

    protected val plugin: NicoYazawa = NicoYazawa.getPlugin()
    protected val logger: Logger = plugin.getLogger("Listener", javaClass.simpleName)

    protected lateinit var module: M
        private set

    @Suppress("UNCHECKED_CAST")
    fun setModule(module: StandardModule) {
        this.module = module as M
    }
}