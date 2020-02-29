package com.nanabell.sponge.nico.module.sync

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.sync.config.SyncConfig

@RegisterModule(id = "sync-module", name = "Sync Module", dependencies = ["discord-module", "link-module", "activity-module"])
class SyncModule : StandardModule<SyncConfig>()