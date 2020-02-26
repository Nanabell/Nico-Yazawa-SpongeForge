package com.nanabell.sponge.nico.internal.module

import com.nanabell.sponge.nico.extensions.toOptional
import com.nanabell.sponge.nico.internal.config.Config
import com.nanabell.sponge.nico.internal.config.StandardConfigAdapter
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ConfigurableModule<A : StandardConfigAdapter<C>, C : Config>(clazz: KClass<A>) : StandardModule() {

    private val adapter: A = clazz.createInstance()

    final override fun getConfigAdapter(): Optional<AbstractConfigAdapter<*>> {
        return adapter.toOptional()
    }

    fun getTypedConfigAdapter(): A {
        return adapter
    }

    fun getConfigOrDefault(): C {
        return adapter.nodeOrDefault
    }

}
