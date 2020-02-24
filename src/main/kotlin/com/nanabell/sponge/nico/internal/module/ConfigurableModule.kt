package com.nanabell.sponge.nico.internal.module

import com.nanabell.sponge.nico.extensions.toOptional
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter
import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter
import java.util.*

abstract class ConfigurableModule<A : TypedAbstractConfigAdapter.StandardWithSimpleDefault<*>>(private val adapter: A) : StandardModule() {

    final override fun getConfigAdapter(): Optional<AbstractConfigAdapter<*>> {
        return adapter.toOptional()
    }

    fun getTypedConfigAdapter(): A {
        return adapter
    }

}
