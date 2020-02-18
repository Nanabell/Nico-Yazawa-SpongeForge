package com.nanabell.sponge.nico.economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class NicoTransactionResult implements TransactionResult {

    private final Account account;
    private final BigDecimal amount;
    private final TransactionType transactionType;
    private final ResultType resultType;

    public NicoTransactionResult(Account account, BigDecimal amount, TransactionType transactionType) {
        this.account = account;
        this.amount = amount;
        this.transactionType = transactionType;
        this.resultType = ResultType.SUCCESS;
    }

    public NicoTransactionResult(Account account, BigDecimal amount, TransactionType transactionType, ResultType resultType) {
        this.account = account;
        this.amount = amount;
        this.transactionType = transactionType;
        this.resultType = resultType;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Currency getCurrency() {
        return NicoCurrency.getCurrency();
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Set<Context> getContexts() {
        return new HashSet<>();
    }

    @Override
    public ResultType getResult() {
        return resultType;
    }

    @Override
    public TransactionType getType() {
        return transactionType;
    }
}
