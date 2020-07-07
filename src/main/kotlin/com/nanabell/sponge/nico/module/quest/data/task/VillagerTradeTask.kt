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
class VillagerTradeTask(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    override val type: String = "VillagerTradeTask"
    override fun newProgress(userId: UUID): ITaskProgress = VillagerTradeProgress(id, 0)
    override fun getName(): Text = "Villager Trade Task".green()
    override fun getMessage(): Text = "Trade $amount times with a Villager".yellow()

    override fun printSettings(): List<Text> {
        return listOf(
                "Amount: ".green().concat(amount.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit amount $id ")))
        )
    }

    override fun copy(id: UUID): ITask = VillagerTradeTask(id, amount)
}

@ConfigSerializable
class VillagerTradeProgress(

        id: UUID,

        @Setting("amount")
        var amount: Int

) : TaskProgress(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    override val type: String = "VillagerTradeTask"
    override fun getTask(): ITask = taskRegistry.get(id)
    fun inc() = amount++
    override fun isComplete(): Boolean = amount >= (getTask() as VillagerTradeTask).amount
    override fun getText(): Text = "[$amount/${(getTask() as VillagerTradeTask).amount}]".yellow()
    override fun copy(id: UUID): ITaskProgress = VillagerTradeProgress(id, amount)
}