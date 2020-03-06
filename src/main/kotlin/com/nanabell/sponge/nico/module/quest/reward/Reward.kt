package com.nanabell.sponge.nico.module.quest.reward

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause

@ConfigSerializable
abstract class Reward {

    @Setting("claimed")
    private var claimed: Boolean = false

    fun claim(player: Player, cause: Cause) {
        if (!claimed)
            claimReward(player, cause).also { claimed = true }
    }

    fun reset() {
        claimed = false
    }

    protected abstract fun claimReward(player: Player, cause: Cause)

    abstract class Builder<R : Reward, B : Builder<R, B>> {

        abstract fun build(): R

        abstract fun getThis(): B

    }

}
