package com.nanabell.sponge.nico.module.core

import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.module.core.config.CoreConfigAdapter
import uk.co.drnaylor.quickstart.annotations.ModuleData

@ModuleData(id = "core", name = "Core Module")
class CoreModule : ConfigurableModule<CoreConfigAdapter>(CoreConfigAdapter()) {

    override fun performPreEnable() {
        logger.info("Constructing Module...")
    }

    override fun performEnable() {
        logger.info("Hello World!")
    }

    override fun performPostEnable() {
        logger.info("Construction complete!")
    }


}