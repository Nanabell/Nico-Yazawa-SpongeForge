package com.nanabell.sponge.nico.module.afk

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig
import com.nanabell.sponge.nico.module.afk.config.AfkConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "afk-module", name = "Afk Module")
class AfkModule : ConfigurableModule<AfkConfigAdapter, AfkConfig>(AfkConfigAdapter::class)