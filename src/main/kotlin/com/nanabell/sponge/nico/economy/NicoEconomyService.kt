package com.nanabell.sponge.nico.economy;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.storage.Persistable;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class NicoEconomyService implements EconomyService {

    private final Persistable<NicoAccount> accounts = NicoYazawa.getPersistenceManager().getUnchecked(NicoAccount.class);

    @Override
    public Currency getDefaultCurrency() {
        return NicoCurrency.getCurrency();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return new HashSet<Currency>() {{
            add(NicoCurrency.getCurrency());
        }};
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return accounts.exists(uuid);
    }

    @Override
    public boolean hasAccount(String identifier) {
        return false; // Unsupported! NicoAccounts are always User-bound.
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        return Optional.of(accounts.getOrCreate(uuid));
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        return Optional.empty(); // Unsupported! NicoAccounts are always User-bound.
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }
}
