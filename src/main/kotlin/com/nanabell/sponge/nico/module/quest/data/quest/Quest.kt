package com.nanabell.sponge.nico.module.quest.data.quest

import com.nanabell.sponge.nico.module.quest.interfaces.IQuest
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
abstract class Quest(

        @Setting("quest-id")
        override val id: UUID,

        @Setting("name")
        override val name: String,

        @Setting("description")
        override val description: String?,

        @Setting("tasks")
        override val tasks: List<UUID>,

        @Setting("rewards")
        override val rewards: List<UUID>,

        @Setting("dependencies")
        override val dependencies: List<UUID>

) : IQuest