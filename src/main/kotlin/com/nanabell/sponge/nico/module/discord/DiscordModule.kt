package com.nanabell.sponge.nico.module.discord

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.discord.config.DiscordConfig
import com.nanabell.sponge.nico.module.discord.config.DiscordConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "discord-module", name = "Discord Module")
class DiscordModule : ConfigurableModule<DiscordConfigAdapter, DiscordConfig>(DiscordConfigAdapter::class)