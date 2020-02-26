package com.nanabell.sponge.nico.module.economy.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.toMinecraftUser
import com.nanabell.sponge.nico.extensions.toOptional
import com.nanabell.sponge.nico.internal.annotation.service.ApiService
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.economy.EconomyModule
import com.nanabell.sponge.nico.module.economy.data.account.DelegatingAccount
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import com.nanabell.sponge.nico.module.economy.data.currency.NicoCurrency
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.service.context.ContextCalculator
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import java.util.*

@ApiService
@RegisterService(EconomyService::class, true)
class NicoEconomyService : AbstractService<EconomyModule>(), EconomyService {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun onPreEnable() {
    }

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
        return getAccount(uuid.toString())
    }

    @Suppress("UNCHECKED_CAST")
    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        return getAccount(identifier) as Optional<Account>
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {
    }

    private fun getAccount(identifier: String): Optional<UniqueAccount> {
        val user = identifier.toMinecraftUser() ?: return Optional.empty()
        val builder = DelegatingAccount.builder(module.getConfigOrDefault())

        val link = linkService.getLink(user)
        if (link != null)
            builder.addNicoCurrency(link.discordId)

        return builder.build(user).toOptional()
    }
}