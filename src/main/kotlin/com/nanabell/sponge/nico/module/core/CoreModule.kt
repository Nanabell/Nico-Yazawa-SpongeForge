package com.nanabell.sponge.nico.module.core

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.core.config.CoreConfig

@RegisterModule(id = "core-module", name = "Core Module")
class CoreModule : StandardModule<CoreConfig>()