package com.nanabell.sponge.nico.module.quest.task

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.entity.living.Living

@ConfigSerializable
class KillTask(

        @Setting("amount")
        private val amount: Int

) : Task() {

    @Suppress("unused")
    constructor(): this(0)

    @Setting("kills")
    private var kills = 0

    fun confirmKill(living: Living) {
        kills++
    }

    fun revokeKill(living: Living) {
        kills--
    }

    override fun isComplete(): Boolean {
        return kills >= amount
    }

    override fun reset() {
        kills = 0
    }

    @Suppress("unused")
    class Builder : Task.Builder<KillTask, Builder>() {

        private var amount: Int = 0

        fun setAmount(amount: Int): Builder {
            this.amount = amount

            return getThis()
        }

        override fun build(): KillTask {
            return KillTask(amount)
        }

        override fun getThis(): Builder {
            return this
        }

    }

    companion object {
        fun builder() = Builder()
    }

}
