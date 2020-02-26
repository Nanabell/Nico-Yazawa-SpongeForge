package com.nanabell.sponge.nico.internal.config

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter
import kotlin.reflect.KClass

abstract class StandardConfigAdapter<T : Config>(clazz: KClass<T>) : TypedAbstractConfigAdapter.StandardWithSimpleDefault<T>(clazz.java)