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
        var amount: Int,

        @Setting("target")
        var target: String?

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0, null)

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
                        .action(TextActions.suggestCommand("/task edit amount $id "))),

                "Target: ".green().concat((target?.split(":")?.get(1)?.capitalize() ?: "Any").yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit mob $id ")))
        )
    }

    override fun copy(id: UUID): ITask {
        return KillTask(id, amount, target)
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
    override fun getTask(): ITask = taskRegistry.get(id)

    fun inc() = amount++

    override fun isComplete(): Boolean {
        return this.amount >= (getTask() as KillTask).amount
    }

    override fun getText(): Text {
        return "[$amount/${(getTask() as KillTask).amount}]".yellow()
    }

    override fun copy(id: UUID): ITaskProgress {
        return KillProgress(id, amount)
    }

}
