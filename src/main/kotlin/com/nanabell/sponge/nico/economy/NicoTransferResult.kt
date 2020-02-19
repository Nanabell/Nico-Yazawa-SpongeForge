package com.nanabell.sponge.nico.economy

import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionType
import org.spongepowered.api.service.economy.transaction.TransferResult
import java.math.BigDecimal
import java.util.*

data class NicoTransferResult(
        private val account: Account,
        private val to: Account,
        private val amount: BigDecimal,
        private val transactionType: TransactionType,
        private val resultType: ResultType
) : TransferResult {

    override fun getAccountTo(): Account {
        return to
    }

    override fun getAccount(): Account {
        return account
    }

    override fun getCurrency(): Currency {
        return NicoCurrency.currency
    }

    override fun getAmount(): BigDecimal {
        return amount
    }

    override fun getContexts(): Set<Context> {
        return HashSet()
    }

    override fun getResult(): ResultType {
        return resultType
    }

    override fun getType(): TransactionType {
        return transactionType
    }

}