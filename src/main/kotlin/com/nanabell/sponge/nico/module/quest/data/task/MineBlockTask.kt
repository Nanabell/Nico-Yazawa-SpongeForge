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
class MineBlockTask(
        id: UUID,

        @Setting("amount")
        var amount: Int,

        @Setting("target")
        var target: String?

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0, null)

    override val type: String = "MineBlockTask"
    override fun newProgress(userId: UUID): ITaskProgress = MineBlockProgress(id, 0)
    override fun getName(): Text = "Mine Block(s) Task".green()
    override fun getMessage(): Text = "Mine $amount of ${if (target != null) target else "Any"} Block(s)".yellow()

    override fun printSettings(): List<Text> {
        return listOf(
                "Amount: ".green().concat(amount.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit amount $id "))),
                "Block: ".green().concat((target ?: "Any").yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit block $id ")))
        )
    }

    override fun copy(id: UUID): ITask = MineBlockTask(id, amount, target)
}

class MineBlockProgress(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : TaskProgress(id) {

    override val type: String = "MineBlockTask"
    override fun getTask(): ITask = taskRegistry.get(id)
    override fun isComplete(): Boolean = amount >= (getTask() as MineBlockTask).amount
    override fun getText(): Text = "[$amount/${(getTask() as MineBlockTask).amount}]".yellow()
    override fun copy(id: UUID): ITaskProgress = MineBlockProgress(id, amount)

}