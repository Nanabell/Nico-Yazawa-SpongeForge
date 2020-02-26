package com.nanabell.sponge.nico.module.economy.data.account

import com.nanabell.sponge.nico.module.economy.config.EconomyConfig
import com.nanabell.sponge.nico.module.economy.database.MakiStorage
import com.nanabell.sponge.nico.module.economy.event.AccountBalanceChangeEvent
import com.nanabell.sponge.nico.module.economy.interfaces.CurrencyAccount
import dev.morphia.query.Query
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import java.math.BigDecimal
import java.util.*

class MakiAccount(private val uuid: UUID, private val config: EconomyConfig) : DatabaseAccount(), CurrencyAccount {

    private val event = Sponge.getEventManager()

    private lateinit var parent: Account

    override fun init(parent: Account) {
        this.parent = parent

        if (config.create && !hasBalance(emptySet())) {
            service.save(MakiStorage(uuid, getDefaultBalance(), emptySet()))
        }
    }

    override fun getDefaultBalance(): BigDecimal = BigDecimal(500)

    override fun hasBalance(contexts: Set<Context>): Boolean {
        return getQuery(contexts).find().hasNext()
    }

    override fun getBalance(contexts: Set<Context>): BigDecimal {
        return getQuery(contexts).find().next().balance
    }

    override fun setBalance(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1) return depositTransaction(parent, amount, ResultType.FAILED)
        val old = getBalance(contexts)

        if (event.post(AccountBalanceChangeEvent(old, amount, cause)))
            return depositTransaction(parent, amount, ResultType.FAILED)


        service.update(getQuery(contexts), getSet(amount))
        return depositTransaction(parent, amount, ResultType.SUCCESS)
    }

    override fun resetBalance(cause: Cause, contexts: Set<Context>): TransactionResult {
        val old = getBalance(contexts)

        if (event.post(AccountBalanceChangeEvent(old, getDefaultBalance(), cause)))
            return withdrawTransaction(parent, getDefaultBalance(), ResultType.FAILED)

        service.update(getQuery(contexts), getSet(getDefaultBalance()))
        return withdrawTransaction(parent, getDefaultBalance(), ResultType.SUCCESS)
    }

    @Suppress("DuplicatedCode")
    override fun deposit(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1)
            return depositTransaction(parent, amount, ResultType.FAILED)

        val old = getBalance(contexts)
        val added = old.add(amount)

        if (event.post(AccountBalanceChangeEvent(old, added, cause)))
            return depositTransaction(parent, added, ResultType.FAILED)

        service.update(getQuery(contexts), getUpdate(amount))
        return depositTransaction(parent, added, ResultType.SUCCESS)
    }

    @Suppress("DuplicatedCode")
    override fun withdraw(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount.signum() == -1)
            return depositTransaction(parent, amount, ResultType.FAILED)

        val old = getBalance(contexts)
        val subtracted = old.subtract(amount)

        if (subtracted < BigDecimal.ZERO) {
            return withdrawTransaction(parent, BigDecimal.ZERO, ResultType.ACCOUNT_NO_FUNDS)
        }

        if (event.post(AccountBalanceChangeEvent(old, subtracted, cause)))
            return withdrawTransaction(parent, subtracted, ResultType.FAILED)

        service.update(getQuery(contexts), getUpdate(-amount))
        return withdrawTransaction(parent, subtracted, ResultType.SUCCESS)
    }

    private fun getUpdate(balance: BigDecimal) = getIncrement<MakiStorage>("balance", balance)
    private fun getSet(balance: BigDecimal) = getSet<MakiStorage>("balance", balance)
    private fun getQuery(contexts: Set<Context>): Query<MakiStorage> {
        val query = getQuery<MakiStorage>("uuid", uuid)
        if (contexts.isNotEmpty())
            query.field("contexts").hasAnyOf(contexts)

        return query
    }

}
