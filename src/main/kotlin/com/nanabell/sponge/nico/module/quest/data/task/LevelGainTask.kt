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
class LevelGainTask(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    override val type: String = "LevelGainTask"

    override fun newProgress(userId: UUID): ITaskProgress {
        return LevelGainProgress(id, 0)
    }

    override fun getName(): Text = "Gain Levels Task".green()

    override fun getMessage(): Text = "Gain $amount Level/s".yellow()

    override fun printSettings(): List<Text> {
        return listOf(
                "Amount: ".green().concat(amount.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit amount $id ")))
        )
    }

    override fun copy(id: UUID): ITask = LevelGainTask(id, amount)

}

class LevelGainProgress(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : TaskProgress(id) {

    override val type: String = "LevelGainTask"
    override fun getTask(): ITask = taskRegistry.get(id)

    override fun isComplete(): Boolean {
        return this.amount >= (getTask() as LevelGainTask).amount
    }

    override fun getText(): Text {
        return "[$amount/${(getTask() as LevelGainTask).amount}]".yellow()
    }

    override fun copy(id: UUID): ITaskProgress = LevelGainProgress(id, amount)

}