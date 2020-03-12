package com.nanabell.sponge.nico.module.quest.data.quest

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.quest.interfaces.IQuest
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
) : Quest<SimpleQuest>(id, name, description, tasks, rewards, dependencies) {

    override val type: String = ""
    override val token: TypeToken<IQuest<SimpleQuest>> = TypeToken.of(SimpleQuest::class.java)

}