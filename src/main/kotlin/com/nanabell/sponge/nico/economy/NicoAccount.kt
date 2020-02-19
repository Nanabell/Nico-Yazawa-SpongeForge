package com.nanabell.sponge.nico.economy

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.nanabell.sponge.nico.storage.IdentifiableDaoEnabled
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import org.spongepowered.api.service.economy.transaction.TransactionTypes
import org.spongepowered.api.service.economy.transaction.TransferResult
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import java.math.BigDecimal
import java.sql.SQLException
import java.util.*

@DatabaseTable(tableName = "nico-accounts")
class NicoAccount : IdentifiableDaoEnabled<NicoAccount>(), UniqueAccount {

    private val serviceManager = Sponge.getServiceManager()

    @DatabaseField(id = true)
    override lateinit var uuid: UUID

    @DatabaseField(defaultValue = "0")
    private lateinit var balance: BigDecimal


    override fun getDisplayName(): Text {
        return Text.of(serviceManager.provideUnchecked(UserStorageService::class.java)[uuid].map { it.name }.orElse(uuid.toString()))
    }

    override fun getDefaultBalance(currency: Currency): BigDecimal {
        return BigDecimal(0)
    }

    override fun hasBalance(currency: Currency, contexts: Set<Context>): Boolean {
        return true // Ignoring Currency and Context for simplicity as of now
    }

    override fun getBalance(currency: Currency, contexts: Set<Context>): BigDecimal {
        return this.balance
    }

    override fun getBalances(contexts: Set<Context>): Map<Currency, BigDecimal> {
        return mapOf(NicoCurrency.currency to this.balance)
    }

    override fun setBalance(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount < BigDecimal.ZERO) {
            return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.FAILED)
        }

        this.balance = amount
        save()

        return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS)
    }

    override fun resetBalances(cause: Cause, contexts: Set<Context>): Map<Currency, TransactionResult> {
        return mapOf(NicoCurrency.currency to resetBalance(NicoCurrency.currency, cause, contexts))
    }

    override fun resetBalance(currency: Currency, cause: Cause, contexts: Set<Context>): TransactionResult {
        this.balance = getDefaultBalance(currency)
        save()

        return NicoTransactionResult(this, balance, TransactionTypes.WITHDRAW, ResultType.SUCCESS
        )
    }

    override fun deposit(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        balance = balance.add(amount)
        save()

        return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS)
    }

    override fun withdraw(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (balance.subtract(amount) < BigDecimal.ZERO) {
            return NicoTransactionResult(this, amount, TransactionTypes.WITHDRAW, ResultType.ACCOUNT_NO_FUNDS)
        }

        balance = balance.subtract(amount)
        save()

        return NicoTransactionResult(this, amount, TransactionTypes.WITHDRAW, ResultType.SUCCESS)
    }

    override fun transfer(to: Account, currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransferResult {
        val withdrawResult = withdraw(currency, amount, cause, contexts)
        if (withdrawResult.result != ResultType.ACCOUNT_NO_FUNDS) {
            return NicoTransferResult(this, to, amount, TransactionTypes.TRANSFER, withdrawResult.result)
        }

        val depositResult = to.deposit(currency, amount, cause, contexts)
        return NicoTransferResult(this, to, amount, TransactionTypes.TRANSFER, depositResult.result)
    }

    override fun getIdentifier(): String {
        return uuid.toString()
    }

    override fun getActiveContexts(): Set<Context> {
        return HashSet()
    }

    override fun getUniqueId(): UUID {
        return uuid
    }

    private fun save() {
        try {
            super.update()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}