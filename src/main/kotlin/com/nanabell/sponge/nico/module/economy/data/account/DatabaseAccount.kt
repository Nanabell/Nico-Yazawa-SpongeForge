package com.nanabell.sponge.nico.module.economy.data.account

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.database.DataEntry
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import com.nanabell.sponge.nico.module.economy.data.SimpleTransactionResult
import com.nanabell.sponge.nico.module.economy.interfaces.CurrencyAccount
import dev.morphia.query.Query
import dev.morphia.query.UpdateOperations
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import org.spongepowered.api.service.economy.transaction.TransactionTypes
import java.math.BigDecimal

abstract class DatabaseAccount : CurrencyAccount {

    protected val service: DatabaseService = NicoYazawa.getServiceRegistry().provideUnchecked(DatabaseService::class)

    protected inline fun <reified T : DataEntry> getQuery(field: String, value: Any): Query<T> = service.findQuery<T>(field, value).field(field).equal(value)
    protected inline fun <reified T : DataEntry> getIncrement(field: String, value: Number): UpdateOperations<T> = service.newUpdateOperations<T>().inc(field, value)
    protected inline fun <reified T : DataEntry> getSet(field: String, value: Number): UpdateOperations<T> = service.newUpdateOperations<T>().set(field, value)


    protected fun depositTransaction(parent: Account, amount: BigDecimal, resultType: ResultType): TransactionResult {
        return SimpleTransactionResult(parent, amount, TransactionTypes.DEPOSIT, resultType)
    }

    protected fun withdrawTransaction(parent: Account, amount: BigDecimal, resultType: ResultType): TransactionResult {
        return SimpleTransactionResult(parent, amount, TransactionTypes.WITHDRAW, resultType)
    }
}