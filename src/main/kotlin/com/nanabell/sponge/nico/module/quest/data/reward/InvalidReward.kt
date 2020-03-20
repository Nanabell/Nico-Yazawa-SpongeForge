package com.nanabell.sponge.nico.module.quest.data.reward

import com.nanabell.sponge.nico.internal.extension.darkRed
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import java.util.*

class InvalidReward(override val id: UUID) : IReward {

    override val type: String = "!INVALID!"
    override fun reward(userId: UUID, cause: Cause) = Unit
    override fun isAttached(): Boolean = true
    override fun getName(): Text = "!INVALID_REWARD!".darkRed()
    override fun getMessage(): Text = "[ERR]".darkRed()
    override fun printSettings(): List<Text> = emptyList()
    override fun copy(id: UUID): IReward = this

}
