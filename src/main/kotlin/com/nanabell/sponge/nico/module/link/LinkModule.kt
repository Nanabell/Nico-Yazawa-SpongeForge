package com.nanabell.sponge.nico.module.link

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.link.config.LinkConfig

@RegisterModule(id = "link-module", name = "Link Module", dependencies = ["discord-module", "core-module"])
class LinkModule : StandardModule<LinkConfig>()