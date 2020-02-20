package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.store.UserData
import dev.morphia.Datastore
import dev.morphia.query.Query
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import org.spongepowered.api.service.economy.account.VirtualAccount
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.economy.transaction.TransactionResult
import org.spongepowered.api.service.economy.transaction.TransactionTypes
import org.spongepowered.api.service.economy.transaction.TransferResult
import org.spongepowered.api.text.Text
import java.math.BigDecimal
import java.util.*


class NicoAccount(private val userId: String): UniqueAccount {

    private val dataStore = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)
    private val discordService = Sponge.getServiceManager().provideUnchecked(DiscordService::class.java)

    override fun getDisplayName(): Text {
        return (discordService.getUserTagById(retrieveUserData()?.userId) ?: "Unable to contact Database. User: ($userId)").toText()
    }

    override fun getUniqueId(): UUID? {
        return null
    }

    override fun getDefaultBalance(currency: Currency): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun hasBalance(currency: Currency, contexts: Set<Context>): Boolean {
        return true // Ignoring Currency and Context for simplicity as of now
    }

    override fun getBalance(currency: Currency, contexts: Set<Context>): BigDecimal {
        return retrieveUserData()?.score?.toBigDecimal() ?: BigDecimal(-1)
    }

    override fun getBalances(contexts: Set<Context>): Map<Currency, BigDecimal> {
        return mapOf(NicoCurrency.currency to getBalance(NicoCurrency.currency, contexts))
    }

    override fun setBalance(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        if (amount < BigDecimal.ZERO) {
            return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.FAILED)
        }

        setUserScore(amount)
        return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS)
    }

    override fun resetBalances(cause: Cause, contexts: Set<Context>): Map<Currency, TransactionResult> {
        return mapOf(NicoCurrency.currency to resetBalance(NicoCurrency.currency, cause, contexts))
    }

    override fun resetBalance(currency: Currency, cause: Cause, contexts: Set<Context>): TransactionResult {
        return setBalance(currency, getDefaultBalance(currency), cause, contexts)
    }

    override fun deposit(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        incUserScore(amount)

        return NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS)
    }

    override fun withdraw(currency: Currency, amount: BigDecimal, cause: Cause, contexts: Set<Context>): TransactionResult {
        val balance = getBalance(currency, contexts)
        if (balance.subtract(amount) < BigDecimal.ZERO) {
            return NicoTransactionResult(this, amount, TransactionTypes.WITHDRAW, ResultType.ACCOUNT_NO_FUNDS)
        }

        incUserScore(-amount)
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
        return userId
    }

    override fun getActiveContexts(): Set<Context> {
        return HashSet()
    }

    private fun retrieveUserData(): UserData? {
        return getQuery().first()
    }

    private fun setUserScore(score: BigDecimal) {
        val ops = dataStore.createUpdateOperations(UserData::class.java).set("score", score.toInt())
        dataStore.update(getQuery(), ops)
    }

    private fun incUserScore(score: BigDecimal) {
        val ops = dataStore.createUpdateOperations(UserData::class.java).inc("score", score.toInt())
        dataStore.update(getQuery(), ops)
    }

    private fun getQuery(): Query<UserData> = dataStore.createQuery(UserData::class.java).field("userId").equal(userId)

}