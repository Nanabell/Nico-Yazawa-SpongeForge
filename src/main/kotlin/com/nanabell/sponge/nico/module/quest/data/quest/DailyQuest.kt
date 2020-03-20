package com.nanabell.sponge.nico.module.quest.data.quest

import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
class DailyQuest(
        id: UUID,
        name: String,
        description: String?,
        tasks: MutableList<UUID>,
        rewards: MutableList<UUID>,
        dependencies: MutableList<UUID>
) : Quest(id, name, description, tasks, rewards, dependencies) {

    @Suppress("unused")
    private constructor(): this(UUID.randomUUID(), "", "", mutableListOf(), mutableListOf(), mutableListOf())

    constructor(id: UUID, name: String, description: String?) : this(id,name, description, mutableListOf(), mutableListOf(), mutableListOf())

    override val type: String = "Daily Quest"

    override fun copy(id: UUID): IQuest {
        return DailyQuest(id, name, description, tasks, rewards, dependencies)
    }
}