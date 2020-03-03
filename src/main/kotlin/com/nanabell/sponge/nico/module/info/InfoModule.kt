package com.nanabell.sponge.nico.module.info

import com.nanabell.quickstart.RegisterModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import com.nanabell.sponge.nico.module.info.config.InfoConfig

@RegisterModule(id = "info-module", name = "Info Module", softDependencies = ["activity-module", "afk-module", "economy-module", "link-module"])
class InfoModule : StandardModule<InfoConfig>() {

    companion object {
        const val PS_EXTRA_PLAYTIME = "extra.playtime"
        const val PS_EXTRA_ACTIVITY = "extra.activity"
        const val PS_EXTRA_ECONOMY = "extra.economy"
        const val PS_EXTRA_LINK = "extra.link"
    }

}
