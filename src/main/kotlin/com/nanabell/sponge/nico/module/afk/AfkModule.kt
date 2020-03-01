package com.nanabell.sponge.nico.module.afk

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig

@RegisterModule(id = "afk-module", name = "Afk Module")
class AfkModule : StandardModule<AfkConfig>() {

    companion object {
        const val O_AFK_TIMEOUT = "nico.afk.timeout"
        const val P_AFK_EXEMPT = "nico.afk.exempt"
    }

}
