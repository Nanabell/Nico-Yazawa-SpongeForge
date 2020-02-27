package com.nanabell.sponge.nico.module.activity

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.activity.config.ActivityConfig
import com.nanabell.sponge.nico.module.activity.config.ActivityConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "activity-module", name = "Activity Module", softDependencies = ["afk-module"], dependencies = ["core-module", "economy-module"])
class ActivityModule : ConfigurableModule<ActivityConfigAdapter, ActivityConfig>(ActivityConfigAdapter::class)