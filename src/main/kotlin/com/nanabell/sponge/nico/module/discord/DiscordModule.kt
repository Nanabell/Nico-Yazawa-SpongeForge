package com.nanabell.sponge.nico.module.discord

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.discord.config.DiscordConfig

@RegisterModule(id = "discord-module", name = "Discord Module")
class DiscordModule : StandardModule<DiscordConfig>()