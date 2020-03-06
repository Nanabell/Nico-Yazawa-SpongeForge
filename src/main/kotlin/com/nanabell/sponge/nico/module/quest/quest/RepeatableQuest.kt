package com.nanabell.sponge.nico.module.quest.quest

import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.reward.Reward
import com.nanabell.sponge.nico.module.quest.task.Task
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.text.Text
import java.util.*

@ConfigSerializable
open class RepeatableQuest(
        uniqueId: UUID,
        name: String,
        description: String?,
        tasks: List<Task>,
        rewards: List<Reward>,
        requirements: List<UUID>,

        @Setting("repeats")
        private val repeats: Int

) : SimpleQuest(uniqueId, name, description, tasks, rewards, requirements) {

    @Suppress("unused")
    constructor() : this(UUID.randomUUID(), "", "", emptyList(), emptyList(), emptyList(), 0)

    @Setting("repeat-count")
    private var repeatCount = 1

    fun reset(): Boolean {
        if (repeats < 0 || repeatCount < repeats) {
            tasks.forEach { it.reset() }
            rewards.forEach { it.reset() }

            if (repeats >= 0)
                repeatCount++

            return true
        }

        return false
    }

    class Builder : Quest.Builder<RepeatableQuest, Builder>() {

        private var repeats: Int = -1

        fun setRepeats(repeats: Int): Builder {
            this.repeats = repeats

            return getThis()
        }

        override fun build(name: String): RepeatableQuest {
            return RepeatableQuest(id, name, description, tasks, rewards, requirements, repeats)
        }

        override fun getThis(): Builder {
            return this
        }
    }

    override fun getText(): Text {
        if (repeats < 0) {
            return "Infinite Repeatable Quest ".green().concat(descriptionText())
        }

        return "$repeatCount/$repeats Repeatable Quest ".green().concat(descriptionText())
    }

    companion object {
        fun builder() = Builder()
    }

}