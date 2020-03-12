package com.nanabell.sponge.nico.module.quest.quest

import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.reward.Reward
import com.nanabell.sponge.nico.module.quest.task.Task
import org.spongepowered.api.text.Text
import java.util.*

class DailyQuest(
        uniqueId: UUID,
        name: String,
        description: String?,
        tasks: List<Task>,
        rewards: List<Reward>,
        requirements: List<UUID>
) : Quest(uniqueId, name, description, tasks, rewards, requirements) {

    @Suppress("unused")
    constructor() : this(UUID.randomUUID(), "", "", emptyList(), emptyList(), emptyList())

    override fun getText(): Text {
        return "Daily Quest ".green().concat(descriptionText())
    }

    class Builder : Quest.Builder<DailyQuest, Builder>() {

        override fun build(name: String): DailyQuest {
            return DailyQuest(id, name, description, tasks, rewards, requirements)
        }

        override fun getThis(): Builder {
            return this
        }
    }

    companion object {
        fun builder() = Builder()
    }

}