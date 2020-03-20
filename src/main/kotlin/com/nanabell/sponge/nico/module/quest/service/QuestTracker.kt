package com.nanabell.sponge.nico.module.quest.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import kotlin.reflect.KClass

@RegisterService
class QuestTracker : AbstractService<QuestModule>() {

    override fun onPreEnable() {
    }

    @Suppress("UNCHECKED_CAST")
    fun <P : ITaskProgress> getActiveProgress(user: User, task: KClass<out ITask>): List<P> {
        val questUser = userRegistry.get(user.uniqueId)
        return questUser.getActiveQuests()
                .flatMap { it.tasks() }
                .filterIsInstance(task.java)
                .map { it.getProgress(user.uniqueId) as P }
                .filter { !it.isComplete() }
    }

    fun commit(user: User) {
        val questUser = userRegistry.get(user.uniqueId)

        questUser.getActiveQuests().forEach { quest ->
            if (quest.tasks().map { it.getProgress(questUser.id) }.all { it.isComplete() }) {
                quest.rewards().forEach { it.reward(questUser.id, Cause.of(EventContext.empty(), this).with(quest, plugin)) }
                questUser.setCompleted(quest.id)
            }
        }

        userRegistry.set(questUser)
    }

    companion object {
        private val userRegistry: UserRegistry by lazy { NicoYazawa.getServiceRegistry().provideUnchecked<UserRegistry>() }
    }
}
