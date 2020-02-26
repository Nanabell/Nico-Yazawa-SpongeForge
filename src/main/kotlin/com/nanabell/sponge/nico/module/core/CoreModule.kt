package com.nanabell.sponge.nico.module.core

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.core.config.CoreConfig
import com.nanabell.sponge.nico.module.core.config.CoreConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "core", name = "Core Module")
class CoreModule : ConfigurableModule<CoreConfigAdapter, CoreConfig>(CoreConfigAdapter::class) {

}