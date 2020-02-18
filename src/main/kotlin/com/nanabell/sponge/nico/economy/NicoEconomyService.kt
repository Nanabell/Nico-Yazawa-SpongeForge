package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.extensions.toOptional
import com.nanabell.sponge.nico.storage.PersistenceManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.context.ContextCalculator
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import java.util.*

class NicoEconomyService : EconomyService {

    private val accounts get() = Sponge.getServiceManager().provideUnchecked(PersistenceManager::class.java).getUnchecked(NicoAccount::class.java)

    override fun getDefaultCurrency(): Currency {
        return NicoCurrency.currency
    }

    override fun getCurrencies(): Set<Currency> {
        return setOf(defaultCurrency)
    }

    override fun hasAccount(uuid: UUID): Boolean {
        return accounts.exists(uuid)
    }

    override fun hasAccount(identifier: String): Boolean {
        return false // Unsupported! NicoAccounts are always User-bound.
    }

    override fun getOrCreateAccount(uuid: UUID): Optional<UniqueAccount> {
        return accounts.getOrCreate(uuid).toOptional()
    }

    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        return Optional.empty() // Unsupported! NicoAccounts are always User-bound.
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {}
}