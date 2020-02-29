package com.nanabell.sponge.nico.module.economy

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.economy.config.EconomyConfig

@RegisterModule(id = "economy-module", name = "Economy Module", dependencies = ["core-module", "link-module"])
class EconomyModule : StandardModule<EconomyConfig>()