package com.nanabell.sponge.nico.module.core.runnable

import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.core.CoreModule

@RegisterRunnable("CoreRunnable", delay = 2)
class CoreRunnable : AbstractRunnable<CoreModule>() {

    override fun run() {
        logger.info("Hello World!")
    }

    override fun onReload() {
    }

}