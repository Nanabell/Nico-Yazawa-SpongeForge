package com.nanabell.sponge.nico.module.afk.listener

import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.broadcast
import com.nanabell.sponge.nico.internal.extension.darkGray
import com.nanabell.sponge.nico.internal.extension.italic
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.event.PlayerAFKEvent
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.event.Listener

@RegisterListener
class AfkListener : AbstractListener<AfkModule>() {

    @Listener
    fun onAfk(event: PlayerAFKEvent) {
        "${event.targetEntity.name} is now AFK".toText().italic().darkGray().broadcast()
    }

    @Listener
    fun onActive(event: PlayerActiveEvent) {
        "${event.targetEntity.name} is no longer AFK".toText().italic().darkGray().broadcast()
    }

}
