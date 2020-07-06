package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.quest.data.user.TaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.Sponge
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@ConfigSerializable
class PlaytimeTask (

        id: UUID,

        @Setting("duration")
        private var _amount: Long

) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), 0)

    var duration: Duration
        get() = Duration.of(_amount, ChronoUnit.MINUTES)
        set(value) {
            _amount = value.toMinutes()
        }

    override val type: String = "PlaytimeTask"
    override fun newProgress(userId: UUID): ITaskProgress = PlaytimeProgress(id, userId)
    override fun getName(): Text = "Playtime Task".green()
    override fun getMessage(): Text = "Play for $duration".yellow()
    override fun printSettings(): List<Text> {
        return listOf(
                "Duration: ".green().concat(duration.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit duration $id ")))
        )
    }
    override fun copy(id: UUID): ITask = PlaytimeTask(id, _amount)
}

class PlaytimeProgress(

        id: UUID,

        val userId: UUID

) : TaskProgress(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), UUID.randomUUID())

    private val playtimeService: PlaytimeService? = NicoYazawa.getServiceRegistry().provide()

    override val type: String = "PlaytimeTask"
    override fun getTask(): ITask = taskRegistry.get(id)
    override fun isComplete(): Boolean = (getTask() as PlaytimeTask).duration <= getPlayerPlaytime()
    override fun getText(): Text = "[${getPlayerPlaytime()?.toString() ?: "UNKNOWN"}/${(getTask() as PlaytimeTask).duration}]".yellow()
    override fun copy(id: UUID): ITaskProgress = PlaytimeProgress(id, userId)

    private fun getPlayerPlaytime(): Duration? {
        val player = Sponge.getServer().getPlayer(userId).orNull() ?: return null
        return playtimeService?.getSessionPlayTime(player)
    }
}