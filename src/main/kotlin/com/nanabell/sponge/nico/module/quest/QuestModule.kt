package com.nanabell.sponge.nico.module.quest

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.quest.config.QuestConfig

@RegisterModule(id = "quest-module", name = "Quest Module", dependencies = ["economy-module"])
class QuestModule : StandardModule<QuestConfig>()