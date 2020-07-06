package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.internal.extension.action
import com.nanabell.sponge.nico.internal.extension.gray
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.apache.commons.lang3.time.DurationFormatUtils
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

    var duration: Duration
        get() = Duration.of(_amount, ChronoUnit.MINUTES)
        set(value) {
            _amount = value.toMinutes()
        }

    override val type: String = "PlaytimeTask"
    override fun newProgress(): ITaskProgress = InvalidProgress(id)
    override fun getName(): Text = "Playtime Task".green()
    override fun getMessage(): Text = "Play for ${DurationFormatUtils.formatDurationWords(_amount, true, true)}".yellow()
    override fun printSettings(): List<Text> {
        return listOf(
                "Amount: ".green().concat(duration.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/task edit amount $id ")))
        )
    }
    override fun copy(id: UUID): ITask = PlaytimeTask(id, _amount)
}