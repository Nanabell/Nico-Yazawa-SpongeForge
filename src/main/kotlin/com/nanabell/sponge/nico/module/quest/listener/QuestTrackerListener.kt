package com.nanabell.sponge.nico.module.quest.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.link.event.LinkedEvent
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.KillProgress
import com.nanabell.sponge.nico.module.quest.data.task.KillTask
import com.nanabell.sponge.nico.module.quest.data.task.LinkDiscordProgress
import com.nanabell.sponge.nico.module.quest.data.task.LinkDiscordTask
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

@RegisterListener
class QuestTrackerListener : AbstractListener<QuestModule>() {

    private val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onEntityDeath(event: DestructEntityEvent.Death) {
        val damageSource = event.cause.first(EntityDamageSource::class.java).orNull()
        if (damageSource != null && damageSource.source is Player) {
            val pes = tracker.getActiveProgress<KillProgress>(damageSource.source as Player, KillTask::class)
            pes.forEach { it.inc() }
            tracker.commit(damageSource.source as Player)
        }
    }

    @Listener
    fun onAccountLinked(event: LinkedEvent) {
        val progresses = tracker.getActiveProgress<LinkDiscordProgress>(event.minecraftUser, LinkDiscordTask::class)
        progresses.forEach { it.linked = true }
        tracker.commit(event.minecraftUser)
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        userRegistry.get(event.targetEntity.uniqueId)
    }
}