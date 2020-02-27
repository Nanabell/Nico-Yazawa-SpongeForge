package com.nanabell.sponge.nico.module.sync

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.sync.config.SyncConfig
import com.nanabell.sponge.nico.module.sync.config.SyncConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "sync-module", name = "Sync Module", dependencies = ["discord-module", "link-module", "activity-module"])
class SyncModule : ConfigurableModule<SyncConfigAdapter, SyncConfig>(SyncConfigAdapter::class)