package com.nanabell.sponge.nico.module.quest.quest

import com.nanabell.sponge.nico.internal.extension.action
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.quest.reward.Reward
import com.nanabell.sponge.nico.module.quest.task.Task
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
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
        val requirements: List<UUID> = emptyList()

) {

    @Transient
    private val questRequirements: MutableList<Quest> = ArrayList()

    fun buildRequirements(questList: List<Quest>) {
        questRequirements.clear()

        for (requirement in requirements) {
            questRequirements.add(questList.first { it.uniqueId == requirement })
        }
    }

    open fun isActive(): Boolean {
        return questRequirements.all { it.isComplete() }
    }

    open fun isComplete(): Boolean {
        return tasks.all { it.isComplete() }
    }

    open fun onComplete() {}

    abstract fun getText(): Text

    protected fun descriptionText(): Text {
        var nameText = name.toText().yellow()
        if (description != null)
            nameText = nameText.action(TextActions.showText((description!!).toText()))

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
