package com.nanabell.sponge.nico.module.quest.quest

import com.nanabell.sponge.nico.module.quest.reward.Reward
import com.nanabell.sponge.nico.module.quest.task.Task
import java.time.ZonedDateTime
import java.util.*

class DailyQuest(
        uniqueId: UUID,
        name: String,
        description: String?,
        tasks: List<Task>,
        rewards: List<Reward>,
        requirements: List<UUID>
) : RepeatableQuest(uniqueId, name, description, tasks, rewards, requirements, -1) {

    var dayOfYear = -1

    override fun isActive(): Boolean {
        return super.isActive() && dayOfYear != ZonedDateTime.now().dayOfYear
    }


}