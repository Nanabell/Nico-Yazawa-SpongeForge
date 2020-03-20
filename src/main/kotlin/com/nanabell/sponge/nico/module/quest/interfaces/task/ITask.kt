package com.nanabell.sponge.nico.module.quest.interfaces.task

import com.nanabell.sponge.nico.NicoConstants
import org.spongepowered.api.text.Text
import java.util.*

interface ITask {

    val id: UUID

    val type: String

    fun getProgress(userId: UUID): ITaskProgress
    fun newProgress(): ITaskProgress

    fun isAttached(): Boolean

    fun getName(): Text
    fun getMessage(): Text
    fun getText(): Text {
        return getName().concat(NicoConstants.SPACE).concat(getMessage())
    }

    fun printSettings(): List<Text>

    fun copy(id: UUID): ITask
}