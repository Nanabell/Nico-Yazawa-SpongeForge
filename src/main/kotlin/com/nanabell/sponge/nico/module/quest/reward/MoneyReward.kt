package com.nanabell.sponge.nico.module.quest.reward

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import com.nanabell.sponge.nico.module.quest.quest.Quest
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.text.Text

@ConfigSerializable
class MoneyReward(

        @Setting("currency")
        private val currency: Currency,

        @Setting("amount")
        private val amount: Int

) : Reward() {

    @Suppress("unused")
    constructor() : this(economy.defaultCurrency, 0)

    override fun claimReward(player: Player, cause: Cause) {
        val account = economy.getOrCreateAccount(player.uniqueId).orNull()
        if (account != null) {
            if (account.hasBalance(currency)) {

                val result = account.deposit(currency, amount.toBigDecimal(), Cause.of(EventContext.empty(), this).with(cause))
                if (result.result == ResultType.SUCCESS) {
                    val quest: Quest = cause.first(Quest::class.java).orNull()
                            ?: throw IllegalStateException("Reward cannot find Source Quest in Cause stack!")

                    player.sendMessage(quest.getText()
                            .concat(" complete!".green())
                            .concat(Text.NEW_LINE)
                            .concat("Reward: ".green())
                            .concat(currency.format(amount.toBigDecimal())))
                    return
                }
                throw ClaimFailedException()

            }
            throw ClaimFailedException()
        }

        throw ClaimFailedException()
    }

    @Suppress("unused")
    class Builder : Reward.Builder<MoneyReward, Builder>() {

        private var currency: Currency = MakiCurrency.instance
        private var amount: Int = 0

        fun setCurrency(currency: Currency): Builder {
            this.currency = currency

            return getThis()
        }

        fun setAmount(amount: Int): Builder {
            this.amount = amount

            return getThis()
        }

        override fun build(): MoneyReward {
            return MoneyReward(currency, amount)
        }

        override fun getThis(): Builder {
            return this
        }

    }

    companion object {
        private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

        fun builder() = Builder()
    }

}