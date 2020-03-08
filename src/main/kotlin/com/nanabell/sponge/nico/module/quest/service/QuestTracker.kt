package com.nanabell.sponge.nico.module.quest.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.quest.Quest
import com.nanabell.sponge.nico.module.quest.task.KillTask
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*
import kotlin.collections.HashMap

@RegisterService
class QuestTracker : AbstractService<QuestModule>() {

    private val playerQuests: MutableMap<UUID, List<Quest>> = HashMap()
    private lateinit var questRegistry: QuestRegistry

    override fun onPreEnable() {
    }

    override fun onEnable() {
        questRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    }

    fun entityKilled(player: Player, living: Living) {
        val quests = get(player)
        quests.filter { it.isActive() }
                .flatMap { it.tasks }
                .filterIsInstance<KillTask>()
                .filter { !it.isComplete() }
                .forEach { it.confirmKill(living) }

        recheck(player)
    }

    private fun recheck(player: Player) {
        val quests = get(player)

        quests.forEach { quest ->
            if (quest.isComplete()) {
                quest.claimRewards(player, Cause.of(EventContext.empty(), this).with(quest, player, plugin))
            }
        }

        if (quests.any { it.changed }) {
            quests.forEach { it.changed = false }
            questRegistry.save(player.uniqueId, quests)
        }
    }

    fun get(player: Player): List<Quest> {
        val quests = playerQuests[player.uniqueId]
        if (quests == null) {
            val loaded = questRegistry.load(player.uniqueId)
            loaded.forEach { it.loadRequirements(loaded) }

            playerQuests[player.uniqueId] = loaded
            return get(player)
        }

        return quests
    }

    fun getAll(): Map<UUID, List<Quest>> {
        return playerQuests.toMap()
    }

}
