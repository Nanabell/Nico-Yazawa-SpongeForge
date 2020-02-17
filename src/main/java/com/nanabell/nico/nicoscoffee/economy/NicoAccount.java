package com.nanabell.nico.nicoscoffee.economy;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nanabell.nico.nicoscoffee.storage.IdentifiableDaoEnabled;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;


@DatabaseTable(tableName = "nico-accounts")
@SuppressWarnings("NotNullFieldNotInitialized")
public class NicoAccount extends IdentifiableDaoEnabled<NicoAccount> implements UniqueAccount {

    private ServiceManager serviceManager = Sponge.getServiceManager();

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField(defaultValue = "0")
    private BigDecimal balance;

    public NicoAccount() {
    }

    @Override
    public Text getDisplayName() {
        return Text.of(serviceManager.provideUnchecked(UserStorageService.class).get(uuid).map(User::getName).orElse(uuid.toString()));
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return new BigDecimal(0);
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return true; // Ignoring Currency and Context for simplicity as of now
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return new HashMap<Currency, BigDecimal>() {{
            put(NicoCurrency.getCurrency(), balance);
        }};
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return new NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.FAILED);
        }

        this.balance = amount;
        save();

        return new NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS);
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        return new HashMap<Currency, TransactionResult>() {{
            put(NicoCurrency.getCurrency(), resetBalance(NicoCurrency.getCurrency(), cause, contexts));
        }};
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        this.balance = BigDecimal.ZERO;
        save();

        return new NicoTransactionResult(this, balance, TransactionTypes.WITHDRAW, ResultType.SUCCESS);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        this.balance = this.balance.add(amount);
        save();

        return new NicoTransactionResult(this, amount, TransactionTypes.DEPOSIT, ResultType.SUCCESS);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (this.balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            return new NicoTransactionResult(this, amount, TransactionTypes.WITHDRAW, ResultType.ACCOUNT_NO_FUNDS);
        }

        this.balance = this.balance.subtract(amount);
        save();

        return new NicoTransactionResult(this, amount, TransactionTypes.WITHDRAW, ResultType.SUCCESS);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult withdrawResult = withdraw(currency, amount, cause, contexts);
        if (withdrawResult.getResult() != ResultType.ACCOUNT_NO_FUNDS) {
            return new NicoTransferResult(this, to, amount, TransactionTypes.TRANSFER, withdrawResult.getResult());
        }

        TransactionResult depositResult = to.deposit(currency, amount, cause, contexts);
        return new NicoTransferResult(this, to, amount, TransactionTypes.TRANSFER, depositResult.getResult());
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    private void save() {
        try {
            super.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
