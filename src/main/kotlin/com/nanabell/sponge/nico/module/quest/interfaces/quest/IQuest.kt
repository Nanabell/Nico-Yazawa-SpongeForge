package com.nanabell.sponge.nico.module.quest.interfaces.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import org.spongepowered.api.text.Text
import java.util.*

interface IQuest {

    val id: UUID
    var name: String
    var description: String?
    val tasks: MutableList<UUID>
    val rewards: MutableList<UUID>
    val dependencies: MutableList<UUID>

    val type: String

    fun tasks(): List<ITask>
    fun rewards(): List<IReward>
    fun dependencies(): List<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest>

    fun isActive(user: UUID): Boolean
    fun isComplete(user: UUID): Boolean

    fun getName(): Text
    fun getMessage(): Text
    fun getText(): Text {
        return getName().concat(NicoConstants.SPACE).concat(getMessage())
    }

    fun copy(id: UUID): com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
}