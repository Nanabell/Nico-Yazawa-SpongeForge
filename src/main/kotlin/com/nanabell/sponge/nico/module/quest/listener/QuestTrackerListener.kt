package com.nanabell.sponge.nico.module.quest.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry_OLD
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class QuestTrackerListener : AbstractListener<QuestModule>() {

    private val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val registry: QuestRegistry_OLD = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onEntityDeath(event: DestructEntityEvent.Death) {
        val damageSource = event.cause.first(EntityDamageSource::class.java).orNull()
        if (damageSource != null && damageSource.source is Player) {
            tracker.entityKilled(damageSource.source as Player, event.targetEntity)
        }
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        tracker.get(event.targetEntity)
    }
}