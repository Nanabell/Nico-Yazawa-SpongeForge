package com.nanabell.sponge.nico.module.quest.data.quest

import com.nanabell.sponge.nico.internal.extension.darkRed
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import org.spongepowered.api.text.Text
import java.util.*

class InvalidQuest(override val id: UUID) : com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest {

    override var name: String = "!INVALID_QUEST!"
    override var description: String? = null
    override val tasks: MutableList<UUID> = mutableListOf()
    override val rewards: MutableList<UUID> = mutableListOf()
    override val dependencies: MutableList<UUID> = mutableListOf()
    override val type: String = "!INVALID!"

    override fun tasks(): List<com.nanabell.sponge.nico.module.quest.interfaces.task.ITask> = emptyList()
    override fun rewards(): List<IReward> = emptyList()
    override fun dependencies(): List<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest> = emptyList()

    override fun isActive(user: UUID): Boolean = false
    override fun isComplete(user: UUID): Boolean = false
    override fun getText(): Text = "!INVALID QUEST!".darkRed()
    override fun getName(): Text = type.darkRed()
    override fun getMessage(): Text = name.darkRed()
    override fun copy(id: UUID): com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest = this
}