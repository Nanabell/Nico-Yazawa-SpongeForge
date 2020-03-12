package com.nanabell.sponge.nico.module.quest.data.quest

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
class SimpleQuest(
        id: UUID,
        name: String,
        description: String,
        tasks: List<UUID>,
        rewards: List<UUID>,
        dependencies: List<UUID>
) : Quest(id, name, description, tasks, rewards, dependencies) {

}