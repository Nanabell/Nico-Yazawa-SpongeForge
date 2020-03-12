package com.nanabell.sponge.nico.module.quest.interfaces

import java.util.*

interface IQuest {

    val id: UUID
    val name: String
    val description: String?
    val tasks: List<UUID>
    val rewards: List<UUID>
    val dependencies: List<UUID>

}