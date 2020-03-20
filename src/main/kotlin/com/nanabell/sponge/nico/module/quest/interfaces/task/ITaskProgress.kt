package com.nanabell.sponge.nico.module.quest.interfaces.task

import java.util.*

interface ITaskProgress {

    val id: UUID

    val type: String

    fun isComplete(): Boolean

    fun copy(id: UUID): ITaskProgress

}