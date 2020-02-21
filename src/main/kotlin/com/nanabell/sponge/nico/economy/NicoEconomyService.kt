package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.NicoYazawa
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

class NicoEconomyService(plugin: NicoYazawa) : EconomyService {

    private val dataStore = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)
    private val configManager = plugin.configManager

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

    @Suppress("DuplicatedCode")
    override fun getOrCreateAccount(uuid: UUID): Optional<UniqueAccount> {
        val link = dataStore.createQuery(Link::class.java).field("minecraftId").equal(uuid).first() ?: return Optional.empty()
        val identifier = link.discordId.toString()
        if (hasAccount(identifier))
            return NicoAccount(identifier).toOptional()

        if (configManager.get().economyConfig.createAccounts) {
            val userData = UserData(identifier, 0, 0)
            dataStore.save(userData)

            return NicoAccount(identifier).toOptional()
        }


        return Optional.empty()
    }

    @Suppress("DuplicatedCode")
    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        if (hasAccount(identifier))
            return NicoAccount(identifier).toOptional()

        if (configManager.get().economyConfig.createAccounts) {
            val userData = UserData(identifier, 0, 0)
            dataStore.save(userData)

            return NicoAccount(identifier).toOptional()
        }

        return Optional.empty()
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {}
}