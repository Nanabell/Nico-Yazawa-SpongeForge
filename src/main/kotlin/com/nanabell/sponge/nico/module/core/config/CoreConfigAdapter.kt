package com.nanabell.sponge.nico.module.core.config

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter

class CoreConfigAdapter : TypedAbstractConfigAdapter.StandardWithSimpleDefault<CoreConfig>(CoreConfig::class.java)