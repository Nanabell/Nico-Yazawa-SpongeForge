package com.nanabell.sponge.nico.module.quest.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.link.event.LinkedEvent
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.*
import com.nanabell.sponge.nico.module.quest.service.QuestTracker
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
import com.nanabell.sponge.nico.module.sync.misc.TroopSource
import com.nanabell.sponge.nico.module.sync.service.TroopSyncService
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.entity.living.humanoid.ChangeLevelEvent
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.event.statistic.ChangeStatisticEvent
import org.spongepowered.api.statistic.Statistics

@RegisterListener
class QuestTrackerListener : AbstractListener<QuestModule>() {

    private val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val tracker: QuestTracker = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val troopService: TroopSyncService? = NicoYazawa.getServiceRegistry().provide()

    @Listener
    fun onBlockBreak(event: ChangeBlockEvent.Break, @Root player: Player) {
        val progresses = tracker.getActiveProgress<MineBlockProgress>(player, MineBlockTask::class)
        progresses.forEach {
            val task = it.getTask() as MineBlockTask
            val blocks = event.transactions.count { transaction -> transaction.original.state.type.id == task.target }
            it.amount += blocks

            tracker.commit(player)
        }
    }

    @Listener
    fun onLevelUp(event: ChangeLevelEvent) {
        val player = event.targetEntity
        if (player is Player && event.level > event.originalLevel) {
            val progress = tracker.getActiveProgress<LevelGainProgress>(player, LevelGainTask::class)
            progress.forEach { it.amount++ }

            tracker.commit(player)
        }
    }

    @Listener
    fun onEntityDeath(event: DestructEntityEvent.Death) {
        val damageSource = event.cause.first(EntityDamageSource::class.java).orNull()
        if (damageSource != null && damageSource.source is Player) {
            val pes = tracker.getActiveProgress<KillProgress>(damageSource.source as Player, KillTask::class)
            pes.forEach {
                val task = it.getTask() as KillTask
                if (task.target == null || task.target == event.targetEntity.type.id)
                    it.amount++
            }

            tracker.commit(damageSource.source as Player)
        }
    }

    @Listener
    fun onAccountLinked(event: LinkedEvent) {
        val progresses = tracker.getActiveProgress<LinkDiscordProgress>(event.minecraftUser, LinkDiscordTask::class)
        progresses.forEach { it.linked = true }
        tracker.commit(event.minecraftUser)

        if (troopService != null) {
            troopService.sync(event.minecraftUser, TroopSource.DISCORD)
            troopService.sync(event.minecraftUser, TroopSource.MINECRAFT)
        }
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        userRegistry.get(event.targetEntity.uniqueId)
    }

    @Listener
    fun onVillagerTradeEvent(event: ChangeStatisticEvent.TargetPlayer) {
        if (event.statistic == Statistics.TRADED_WITH_VILLAGER) {
            val progresses = tracker.getActiveProgress<VillagerTradeProgress>(event.targetEntity, VillagerTradeTask::class)
            progresses.forEach { it.inc() }
            tracker.commit(event.targetEntity)
        }
    }
}