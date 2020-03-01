package com.nanabell.sponge.nico.module.info

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.info.config.InfoConfig

@RegisterModule(id = "info-module", name = "Info Module", softDependencies = ["activity-module", "afk-module", "economy-module", "link-module"])
class InfoModule : StandardModule<InfoConfig>()