package com.nanabell.sponge.nico.module.afk

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig

@RegisterModule(id = "afk-module", name = "Afk Module")
class AfkModule : StandardModule<AfkConfig>()