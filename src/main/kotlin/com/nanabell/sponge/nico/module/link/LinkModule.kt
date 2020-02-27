package com.nanabell.sponge.nico.module.link

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.link.config.LinkConfig
import com.nanabell.sponge.nico.module.link.config.LinkConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "link-module", name = "Link Module", dependencies = ["discord-module", "core-module", "activity-module"])
class LinkModule : ConfigurableModule<LinkConfigAdapter, LinkConfig>(LinkConfigAdapter::class)