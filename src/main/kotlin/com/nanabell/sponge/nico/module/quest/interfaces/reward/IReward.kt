package com.nanabell.sponge.nico.module.quest.interfaces.reward

import com.nanabell.sponge.nico.NicoConstants
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import java.util.*

interface IReward {

    val id: UUID

    val type: String

    fun reward(userId: UUID, cause: Cause)

    fun isAttached(): Boolean

    fun getName(): Text
    fun getMessage(): Text
    fun getText(): Text {
        return getName().concat(NicoConstants.SPACE).concat(getMessage())
    }

    fun printSettings(): List<Text>

    fun copy(id: UUID): IReward
}
