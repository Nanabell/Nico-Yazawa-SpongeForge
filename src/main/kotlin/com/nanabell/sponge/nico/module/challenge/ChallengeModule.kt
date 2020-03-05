package com.nanabell.sponge.nico.module.challenge

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.challenge.config.ChallengeConfig

@RegisterModule(id = "challenge-module", name = "Challenge Module", dependencies = ["economy-module"])
class ChallengeModule : StandardModule<ChallengeConfig>()