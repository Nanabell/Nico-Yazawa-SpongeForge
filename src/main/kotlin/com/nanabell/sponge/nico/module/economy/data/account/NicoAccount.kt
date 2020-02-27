package com.nanabell.sponge.nico.module.economy.data.account

import com.nanabell.sponge.nico.module.economy.config.EconomyConfig
import com.nanabell.sponge.nico.module.economy.database.NicoStorage
import com.nanabell.sponge.nico.module.economy.event.AccountBalanceChangeEvent
import com.nanabell.sponge.nico.module.economy.interfaces.CurrencyAccount
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import java.math.BigDecimal

class NicoAccount(private val userId: Long, private val config: EconomyConfig) : DatabaseAccount(), CurrencyAccount {

    private val event = Sponge.getEventManager()

    private lateinit var parent: Account

    override fun init(parent: Account, contexts: Set<Context>) {
        this.parent = parent

        if (config.create && !hasBalance(contexts)) {
            service.save(NicoStorage(userId.toString(), 0, 0))
        }
    }

    override fun getDefaultBalance(): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun hasBalance(contexts: Set<Context>): Boolean {
        return getQuery().find().hasNext()
    }

    override fun getBalance(contexts: Set<Context>): BigDecimal {
        return getQuery().find().next().balance
    }

    override fun setBalance(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1)
            return depositTransaction(parent, amount, ResultType.FAILED)

        val oldAmount = getBalance(contexts)

        if (event.post(AccountBalanceChangeEvent(oldAmount, amount, cause)))
            return depositTransaction(parent, amount, ResultType.FAILED)

        service.update(getQuery(), getSet(amount))
        return depositTransaction(parent, amount, ResultType.SUCCESS)
    }

    override fun resetBalance(cause: Cause, contexts: Set<Context>): TransactionResult {
        val oldAmount = getBalance(contexts)

        if (event.post(AccountBalanceChangeEvent(oldAmount, getDefaultBalance(), cause)))
            return withdrawTransaction(parent, getDefaultBalance(), ResultType.FAILED)

        service.update(getQuery(), getSet(getDefaultBalance()))
        return withdrawTransaction(parent, getDefaultBalance(), ResultType.SUCCESS)
    }

    @Suppress("DuplicatedCode")
    override fun deposit(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1)
            return depositTransaction(parent, amount, ResultType.FAILED)

        val oldAmount = getBalance(contexts)
        val added = oldAmount.add(amount)

        if (event.post(AccountBalanceChangeEvent(oldAmount, added, cause)))
            return depositTransaction(parent, added, ResultType.FAILED)

        service.update(getQuery(), getUpdate(amount))
        return depositTransaction(parent, added, ResultType.SUCCESS)
    }

    @Suppress("DuplicatedCode")
    override fun withdraw(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1)
            return depositTransaction(parent, amount, ResultType.FAILED)

        val oldAmount = getBalance(contexts)
        val subtracted = oldAmount.subtract(amount)

        if (subtracted < BigDecimal.ZERO) {
            return withdrawTransaction(parent, BigDecimal.ZERO, ResultType.ACCOUNT_NO_FUNDS)
        }

        if (event.post(AccountBalanceChangeEvent(oldAmount, subtracted, cause)))
            return withdrawTransaction(parent, subtracted, ResultType.FAILED)

        service.update(getQuery(), getUpdate(-amount))
        return withdrawTransaction(parent, subtracted, ResultType.SUCCESS)
    }

    private fun getUpdate(balance: BigDecimal) = getIncrement<NicoStorage>("score", balance.toInt())
    private fun getSet(balance: BigDecimal) = getSet<NicoStorage>("score", balance.toInt())
    private fun getQuery() = getQuery<NicoStorage>("userId", userId.toString())
}