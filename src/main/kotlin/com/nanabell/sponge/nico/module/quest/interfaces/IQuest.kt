package com.nanabell.sponge.nico.module.quest.interfaces

import com.google.common.reflect.TypeToken
import java.util.*

interface IQuest<T : IQuest<T>> {

    val id: UUID
    val name: String
    val description: String?
    val tasks: List<UUID>
    val rewards: List<UUID>
    val dependencies: List<UUID>

    val type: String

    val token: TypeToken<IQuest<T>>
}