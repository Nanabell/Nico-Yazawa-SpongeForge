package com.nanabell.sponge.nico.economy.account

import com.nanabell.sponge.nico.economy.SimpleTransactionResult
import dev.morphia.Datastore
import dev.morphia.query.Query
import dev.morphia.query.UpdateOperations
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import org.spongepowered.api.service.economy.transaction.TransactionTypes
import java.math.BigDecimal

abstract class DatabaseAccount : CurrencyAccount {

    protected val dataStore: Datastore = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)

    protected fun <T> getQuery(clazz: Class<T>, field: String, value: Any): Query<T> = dataStore.createQuery(clazz).field(field).equal(value)
    protected fun <T> getUpdate(clazz: Class<T>, field: String, value: Number): UpdateOperations<T> = dataStore.createUpdateOperations(clazz).inc(field, value)
    protected fun <T> getSet(clazz: Class<T>, field: String, value: Number): UpdateOperations<T> = dataStore.createUpdateOperations(clazz).set(field, value)


    protected fun depositTransaction(parent: Account, amount: BigDecimal, resultType: ResultType): TransactionResult {
        return SimpleTransactionResult(parent, amount, TransactionTypes.DEPOSIT, resultType)
    }

    protected fun withdrawTransaction(parent: Account, amount: BigDecimal, resultType: ResultType): TransactionResult {
        return SimpleTransactionResult(parent, amount, TransactionTypes.WITHDRAW, resultType)
    }
}