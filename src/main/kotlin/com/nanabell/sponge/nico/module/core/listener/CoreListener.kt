package com.nanabell.sponge.nico.module.core.listener

import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.core.CoreModule
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent

@RegisterListener
class CoreListener : AbstractListener<CoreModule>() {

    @Listener
    fun onGameLoaded(event: GameStartedServerEvent) {
        logger.info("Hello World!")
    }
}