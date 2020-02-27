package com.nanabell.sponge.nico.module.economy.data.account

import com.nanabell.sponge.nico.internal.extension.MinecraftUser
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.economy.config.EconomyConfig
import com.nanabell.sponge.nico.module.economy.data.SimpleTransferResult
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import com.nanabell.sponge.nico.module.economy.data.currency.NicoCurrency
import com.nanabell.sponge.nico.module.economy.interfaces.CurrencyAccount
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import org.spongepowered.api.service.economy.transaction.TransactionTypes
import org.spongepowered.api.service.economy.transaction.TransferResult
import org.spongepowered.api.text.Text
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class DelegatingAccount(
        private val user: MinecraftUser,
        private val currencyHolders: Map<Currency, CurrencyAccount>
) : UniqueAccount {

    class Builder(private val config: EconomyConfig) {

        private val _holders: MutableMap<Currency, CurrencyAccount> = HashMap()

        fun addNicoCurrency(userId: Long): Builder {
            _holders[NicoCurrency.instance] = NicoAccount(userId, config)

            return this
        }

        fun build(user: MinecraftUser): DelegatingAccount {
            _holders[MakiCurrency.instance] = MakiAccount(user.uniqueId, config)

            return DelegatingAccount(user, _holders)
        }
    }

    init {
        currencyHolders.forEach { it.value.init(this) }
    }

    override fun getUniqueId(): UUID = user.uniqueId
    override fun getIdentifier(): String = user.identifier
    override fun getDisplayName(): Text = user.name.toText()
    override fun getActiveContexts(): Set<Context> = user.activeContexts

    override fun hasBalance(currency: Currency, contexts: Set<Context>): Boolean {
        return currencyHolders.containsKey(currency) && getCurrencyHolder(currency).hasBalance(contexts)
    }

    override fun getDefaultBalance(currency: Currency): BigDecimal {
        return getCurrencyHolder(currency).getDefaultBalance()
    }

    override fun resetBalances(cause: Cause, contexts: Set<Context>): Map<Currency, TransactionResult> {
        return getCurrencyHolders().mapValues { it.value.resetBalance(cause, contexts) }
    }

    override fun getBalance(currency: Currency, contexts: Set<Context>): BigDecimal {
        return getCurrencyHolder(currency).getBalance(contexts)
    }

    override fun getBalances(contexts: MutableSet<Context>): Map<Currency, BigDecimal> {
        return getCurrencyHolders().mapValues { it.value.getBalance(contexts) }
    }

    override fun setBalance(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        return getCurrencyHolder(currency).setBalance(amount, cause, contexts)
    }

    override fun resetBalance(currency: Currency, cause: Cause, contexts: MutableSet<Context>): TransactionResult {
        return getCurrencyHolder(currency).resetBalance(cause, contexts)
    }

    override fun transfer(to: Account, currency: Currency, amount: BigDecimal, cause: Cause, contexts: MutableSet<Context>): TransferResult {
        if (amount.signum() == -1)
            return transferTransaction(to, amount, ResultType.FAILED)

        val withdraw = to.withdraw(currency, amount, cause, contexts)
        if (withdraw.result != ResultType.SUCCESS) {
            return transferTransaction(to, withdraw.amount, withdraw.result)
        }

        val deposit = deposit(currency, amount, cause, contexts)
        if (deposit.result != ResultType.SUCCESS) {
            return transferTransaction(to, deposit.amount, deposit.result)
        }

        return transferTransaction(to, amount, ResultType.SUCCESS)
    }

    override fun deposit(currency: Currency, amount: BigDecimal, cause: Cause, contexts: MutableSet<Context>): TransactionResult {
        return getCurrencyHolder(currency).deposit(amount, cause, contexts)
    }

    override fun withdraw(currency: Currency, amount: BigDecimal, cause: Cause, contexts: MutableSet<Context>): TransactionResult {
        return getCurrencyHolder(currency).withdraw(amount, cause, contexts)
    }

    private fun getCurrencyHolder(currency: Currency): CurrencyAccount {
        return currencyHolders[currency]
                ?: error("Currency Holders does not Contain an Account for Currency: $currency")
    }

    private fun getCurrencyHolders(): Map<Currency, CurrencyAccount> {
        return currencyHolders
    }

    private fun transferTransaction(to: Account, amount: BigDecimal, resultType: ResultType): TransferResult {
        return SimpleTransferResult(this, to, amount, TransactionTypes.TRANSFER, resultType)
    }

    companion object {
        fun builder(config: EconomyConfig): Builder = Builder(config)
    }
}
