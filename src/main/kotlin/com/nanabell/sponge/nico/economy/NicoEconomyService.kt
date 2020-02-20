package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.extensions.toOptional
import com.nanabell.sponge.nico.store.Link
import com.nanabell.sponge.nico.store.UserData
import dev.morphia.Datastore
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.context.ContextCalculator
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import java.util.*

class NicoEconomyService : EconomyService {

    private val dataStore = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)

    override fun getDefaultCurrency(): Currency {
        return NicoCurrency.currency
    }

    override fun getCurrencies(): Set<Currency> {
        return setOf(defaultCurrency)
    }

    override fun hasAccount(uuid: UUID): Boolean {
        val link = dataStore.createQuery(Link::class.java).field("minecraftId").equal(uuid).first() ?: return false

        return hasAccount(link.discordId.toString())
    }

    override fun hasAccount(identifier: String): Boolean {
        return dataStore.createQuery(UserData::class.java).field("userId").equal(identifier).count() == 1L
    }

    override fun getOrCreateAccount(uuid: UUID): Optional<UniqueAccount> {
        val link = dataStore.createQuery(Link::class.java).field("minecraftId").equal(uuid).first()
                ?: return Optional.empty()

        return if (hasAccount(uuid)) NicoAccount(link.discordId.toString()).toOptional() else Optional.empty()
    }

    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        return if (hasAccount(identifier)) NicoAccount(identifier).toOptional() else Optional.empty()
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {}
}