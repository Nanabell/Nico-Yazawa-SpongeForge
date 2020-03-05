package com.nanabell.sponge.nico.module.challenge.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.challenge.ChallengeModule
import com.nanabell.sponge.nico.module.challenge.service.ChallengeService
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.entity.DestructEntityEvent

@RegisterListener
class EntityDeathListener : AbstractListener<ChallengeModule>() {

    private val service: ChallengeService = NicoYazawa.getServiceRegistry().provideUnchecked()

    @Listener
    fun onEntityDeath(event: DestructEntityEvent.Death) {
        val damageSource = event.cause.first(EntityDamageSource::class.java).orNull()
        if (damageSource != null && damageSource is Player) {
            service.onEntityKill(damageSource, event.targetEntity)
        }
    }
}