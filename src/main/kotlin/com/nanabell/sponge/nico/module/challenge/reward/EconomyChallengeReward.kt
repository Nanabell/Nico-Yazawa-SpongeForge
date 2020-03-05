package com.nanabell.sponge.nico.module.challenge.reward

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal

class EconomyChallengeReward(
        private val amount: BigDecimal,
        private val currency: Currency = MakiCurrency.instance
) : ChallengeReward() {

    constructor(amount: Int, currency: Currency = MakiCurrency.instance): this(amount.toBigDecimal(), currency)

    override fun applyReward(user: User, cause: Cause) {
        val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()
        if (economy.currencies.contains(currency)) {

            val account = economy.getOrCreateAccount(user.uniqueId).orNull()
            if (account != null) {
                if (account.hasBalance(currency)) {
                    val result = account.deposit(currency, amount, cause)
                    if (result.result == ResultType.SUCCESS)
                        return
                }
            }
        }
    }

}