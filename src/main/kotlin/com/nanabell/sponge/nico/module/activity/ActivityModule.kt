package com.nanabell.sponge.nico.module.activity

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig

@RegisterModule(id = "activity-module", name = "Activity Module", softDependencies = ["afk-module"], dependencies = ["core-module", "economy-module"])
class ActivityModule : StandardModule<ActivityConfig>()