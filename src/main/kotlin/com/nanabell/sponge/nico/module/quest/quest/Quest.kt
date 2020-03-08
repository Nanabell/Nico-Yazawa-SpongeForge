package com.nanabell.sponge.nico.module.quest.quest

import com.nanabell.sponge.nico.internal.extension.action
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.quest.reward.Reward
import com.nanabell.sponge.nico.module.quest.task.Task
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*
import kotlin.collections.ArrayList

@ConfigSerializable
abstract class Quest(

        @Setting("unique-id")
        val uniqueId: UUID,

        @Setting("name")
        val name: String,

        @Setting("description")
        val description: String?,

        @Setting("tasks")
        val tasks: List<Task>,

        @Setting("rewards")
        val rewards: List<Reward>,

        @Setting("requirements")
        val requirementIds: List<UUID> = emptyList(),

        @Setting("status")
        var status: QuestStatus = QuestStatus.LOCKED

) {

    private val requirements: MutableList<Quest> = ArrayList()
    var changed: Boolean = false

    fun loadRequirements(questList: List<Quest>) {
        requirements.clear()

        for (requirement in requirementIds) {
            requirements.add(questList.first { it.uniqueId == requirement })
        }

        if (requirements.isEmpty()) status = QuestStatus.ACTIVE
    }

    fun isActive(): Boolean = update().let { status == QuestStatus.ACTIVE }
    fun isComplete(): Boolean = update().let { status == QuestStatus.COMPLETED }
    fun isClaimed(): Boolean = update().let { status == QuestStatus.CLAIMED }
    fun isFinished(): Boolean = isComplete() || isClaimed()

    fun claimRewards(player: Player, cause: Cause) {
        rewards.filter { !it.isClaimed() }.forEach { it.claim(player, cause) }.also { update() }
    }

    fun reset() {
        status = QuestStatus.LOCKED.also { changed = true }
        rewards.forEach { it.reset() }
        tasks.forEach { it.reset() }

        update()
    }

    open fun update() {
        if (status == QuestStatus.LOCKED) {
            if (requirements.all { it.isFinished() }) {
                status = QuestStatus.ACTIVE.also { changed = true }
            }
        }

        if (status == QuestStatus.ACTIVE) {
            if (tasks.all { it.isComplete() }) {
                status = QuestStatus.COMPLETED.also { changed = true }
            }
        }

        if (status == QuestStatus.COMPLETED) {
            if (rewards.all { it.isClaimed() }) {
                status = QuestStatus.CLAIMED.also { changed = true }
            }
        }
    }

    abstract fun getText(): Text

    protected fun descriptionText(): Text {
        var nameText = name.toText().yellow()
        if (description != null)
            nameText = nameText.action(TextActions.showText((description).toText()))

        return nameText
    }

    @Suppress("unused")
    abstract class Builder<Q : Quest, B : Builder<Q, B>> {

        protected var id: UUID = UUID.randomUUID()
        protected var description: String? = null
        protected var tasks: MutableList<Task> = mutableListOf()
        protected var rewards: MutableList<Reward> = mutableListOf()
        protected var requirements: MutableList<UUID> = mutableListOf()

        fun setId(id: UUID): B {
            this.id = id

            return getThis()
        }

        fun setDescription(description: String): B {
            this.description = description

            return getThis()
        }

        fun addTask(task: Task): B {
            this.tasks.add(task)

            return getThis()
        }

        fun addTasks(tasks: List<Task>): B {
            this.tasks.addAll(tasks)

            return getThis()
        }

        fun addReward(reward: Reward): B {
            this.rewards.add(reward)

            return getThis()
        }

        fun addRewards(rewards: List<Reward>): B {
            this.rewards.addAll(rewards)

            return getThis()
        }

        fun addRequirement(requirement: UUID): B {
            this.requirements.add(requirement)

            return getThis()
        }

        fun addRequirements(requirements: List<UUID>): B {
            this.requirements.addAll(requirements.map { it })

            return getThis()
        }

        abstract fun build(name: String): Q

        abstract fun getThis(): B

    }
}
