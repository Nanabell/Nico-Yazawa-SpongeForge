package com.nanabell.sponge.nico.module.challenge.reward

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal
import java.util.*

class EconomyChallengeReward(
        userId: UUID,
        private val amount: BigDecimal,
        private val currency: Currency = MakiCurrency.instance
) : ChallengeReward(userId) {

    override fun applyReward(cause: Cause) {
        val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()
        if (economy.currencies.contains(currency)) {

            val account = economy.getOrCreateAccount(userId).orNull()
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