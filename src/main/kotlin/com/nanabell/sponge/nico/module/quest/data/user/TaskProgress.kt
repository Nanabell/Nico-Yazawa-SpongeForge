package com.nanabell.sponge.nico.module.quest.data.user

import com.nanabell.sponge.nico.module.quest.data.RegistryHolder
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
abstract class TaskProgress(

        override val id: UUID

) : RegistryHolder(), ITaskProgress