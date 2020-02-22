package com.nanabell.sponge.nico.economy.account

import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.TransactionResult
import java.math.BigDecimal

interface CurrencyAccount {

    fun init(parent: Account)

    fun getDefaultBalance(): BigDecimal

    fun hasBalance(contexts: Set<Context>): Boolean

    fun getBalance(contexts: Set<Context>): BigDecimal

    fun setBalance(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult

    fun resetBalance(cause: Cause, contexts: Set<Context>): TransactionResult

    fun deposit(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult

    fun withdraw(amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult

}
