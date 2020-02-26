package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.economy.account.DelegatingAccount
import com.nanabell.sponge.nico.economy.currency.MakiCurrency
import com.nanabell.sponge.nico.economy.currency.NicoCurrency
import com.nanabell.sponge.nico.extensions.toMinecraftUser
import com.nanabell.sponge.nico.extensions.toOptional
import com.nanabell.sponge.nico.module.link.database.Link
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
        return MakiCurrency.instance
    }

    override fun getCurrencies(): Set<Currency> {
        return setOf(defaultCurrency, NicoCurrency.instance)
    }

    override fun hasAccount(uuid: UUID): Boolean {
        return uuid.toMinecraftUser() != null
    }

    override fun hasAccount(identifier: String): Boolean {
        return identifier.toMinecraftUser() != null
    }

    override fun getOrCreateAccount(uuid: UUID): Optional<UniqueAccount> {
        val user = uuid.toMinecraftUser() ?: return Optional.empty()
        val builder = DelegatingAccount.builder()

        val link = getLink(user.uniqueId)
        if (link != null)
            builder.addNicoCurrency(link.discordId)

        return builder.build(user).toOptional()
    }

    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        val user = identifier.toMinecraftUser() ?: return Optional.empty()
        val builder = DelegatingAccount.builder()

        val link = getLink(user.uniqueId)
        if (link != null)
            builder.addNicoCurrency(link.discordId)

        return builder.build(user).toOptional()
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {
    }

    private fun getLink(uuid: UUID): Link? {
        return dataStore.createQuery(Link::class.java).field("minecraftId").equal(uuid).first()
    }
}