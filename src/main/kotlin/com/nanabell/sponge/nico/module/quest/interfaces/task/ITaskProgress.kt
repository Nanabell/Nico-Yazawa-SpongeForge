package com.nanabell.sponge.nico.module.quest.interfaces.task

import org.spongepowered.api.text.Text
import java.util.*

interface ITaskProgress {

    val id: UUID

    val type: String

    fun getTask(): ITask

    fun isComplete(): Boolean

    fun getText(): Text

    fun copy(id: UUID): ITaskProgress

}