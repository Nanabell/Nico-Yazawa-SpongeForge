package com.nanabell.sponge.nico.module.economy.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.ApiService
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.extension.toMinecraftUser
import com.nanabell.sponge.nico.internal.extension.toOptional
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.economy.EconomyModule
import com.nanabell.sponge.nico.module.economy.data.account.DelegatingAccount
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import com.nanabell.sponge.nico.module.economy.data.currency.NicoCurrency
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.entity.living.player.User
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
        val user = uuid.toMinecraftUser() ?: return Optional.empty()
        return getAccount(user)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getOrCreateAccount(identifier: String): Optional<Account> {
        val user = identifier.toMinecraftUser() ?: return Optional.empty()
        return getAccount(user) as Optional<Account>
    }

    override fun registerContextCalculator(calculator: ContextCalculator<Account>) {
    }

    private fun getAccount(user: User): Optional<UniqueAccount> {
        val builder = DelegatingAccount.builder(module.getConfigOrDefault(), user.activeContexts)

        val link = linkService.getLink(user)
        if (link != null)
            builder.addNicoCurrency(link.discordId)

        return builder.build(user).toOptional()
    }
}