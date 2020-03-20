package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.internal.extension.action
import com.nanabell.sponge.nico.internal.extension.gray
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.quest.data.user.TaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*

@ConfigSerializable
class KillTask(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    override val type: String = "KillTask"

    override fun newProgress(): ITaskProgress {
        return KillProgress(id, 0)
    }

    override fun getName(): Text = "Kill Task".green()
    override fun getMessage(): Text = "Kill Any $amount Living Creature(s)".yellow()

    override fun printSettings(): List<Text> {
        return listOf(
                "Amount: ".green().concat(amount.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit amount $id")))
        )
    }

    override fun copy(id: UUID): ITask {
        return KillTask(id, amount)
    }
}

@ConfigSerializable
class KillProgress(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : TaskProgress(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    override val type: String = "KillTask"

    fun inc() = amount++

    override fun isComplete(): Boolean {
        return this.amount >= (taskRegistry.get(this.id) as KillTask).amount
    }

    override fun copy(id: UUID): ITaskProgress {
        return KillProgress(id, amount)
    }

}
