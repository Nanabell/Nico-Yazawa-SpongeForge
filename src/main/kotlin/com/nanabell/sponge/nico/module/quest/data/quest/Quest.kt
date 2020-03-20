package com.nanabell.sponge.nico.module.quest.data.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.data.RegistryHolder
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*

@ConfigSerializable
abstract class Quest(

        override val id: UUID,

        @Setting("name")
        override var name: String,

        @Setting("description")
        override var description: String?,

        @Setting("tasks")
        override val tasks: MutableList<UUID>,

        @Setting("rewards")
        override val rewards: MutableList<UUID>,

        @Setting("dependencies")
        override val dependencies: MutableList<UUID>

) : RegistryHolder(), com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest {

    override fun tasks(): List<com.nanabell.sponge.nico.module.quest.interfaces.task.ITask> {
        return tasks.map { taskRegistry.get(it) }
    }

    override fun rewards(): List<IReward> {
        return rewards.map { rewardRegistry.get(it) }
    }

    override fun dependencies(): List<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest> {
        return dependencies.map { questRegistry.get(it) }
    }

    override fun isActive(user: UUID): Boolean {
        if (isComplete(user)) {
            return false
        }

        return dependencies.all { isComplete(it, user) }
    }

    override fun isComplete(user: UUID): Boolean {
        return isComplete(this.id, user)
    }

    override fun getText(): Text {
        val hoverMessage = if (description != null) {
            description!!.gray().concat(Text.NEW_LINE).concat(Text.NEW_LINE).concat("Click for more Infos".darkGray())
        } else {
            "Click for more Infos".darkGray()
        }

        return getName().concat(NicoConstants.SPACE)
                .concat(getMessage()
                        .action(TextActions.showText(hoverMessage))
                        .action(TextActions.runCommand("/quest info $id")))
    }

    override fun getName(): Text {
        return type.green()
    }

    override fun getMessage(): Text {
        return "[$name]".yellow()
    }

    private fun isComplete(questId: UUID, user: UUID): Boolean {
        return userRegistry.get(user).hasCompleted(questId)
    }
}