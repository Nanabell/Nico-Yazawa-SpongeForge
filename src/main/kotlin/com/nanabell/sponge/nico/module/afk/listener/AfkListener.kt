package com.nanabell.sponge.nico.module.afk.listener

import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.darkGray
import com.nanabell.sponge.nico.internal.extension.italic
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.event.PlayerAFKEvent
import com.nanabell.sponge.nico.module.afk.event.PlayerActiveEvent
import org.spongepowered.api.event.Listener
import org.spongepowered.api.text.chat.ChatTypes

@RegisterListener
class AfkListener : AbstractListener<AfkModule>() {

    @Listener
    private fun onAfk(event: PlayerAFKEvent) {
        event.targetEntity.world.messageChannel.send(event.targetEntity, "${event.targetEntity} is now AFK".toText().italic().darkGray(), ChatTypes.SYSTEM)
    }

    @Listener
    private fun onActive(event: PlayerActiveEvent) {
        event.targetEntity.world.messageChannel.send(event.targetEntity, "${event.targetEntity.name} is no longer AFK".toText().italic().darkGray(), ChatTypes.SYSTEM)
    }

}
