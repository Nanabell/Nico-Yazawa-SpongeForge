package com.nanabell.sponge.nico.module.economy

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.economy.config.EconomyConfig
import com.nanabell.sponge.nico.module.economy.config.EconomyConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "economy-module", name = "Economy Module", dependencies = ["core-module", "link-module"])
class EconomyModule : ConfigurableModule<EconomyConfigAdapter, EconomyConfig>(EconomyConfigAdapter::class)