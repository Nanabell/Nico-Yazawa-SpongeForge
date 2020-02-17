package com.nanabell.sponge.nico.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class PaymentConfig {

    @Setting(value = "payment_amount", comment = "Amount Money that is rewarded [0 - 2,147,483,647]")
    private int paymentAmount = 50;

    @Setting(value = "daily_payment_limit", comment = "Maximum amount of currency that can be earned per day [0 = no limit, 2,147,483,647 = max]")
    private int dailyPaymentLimit = 1500;

    @Setting(value = "payment_chance", comment = "Percentage chance for payment [0 = never, 100 = always]")
    private int paymentChance = 100;

    @Setting(value = "required_permission", comment = "Required Permission to be eligible for this Payment [Empty for no requirement]")
    private String requiredPermission;

    public PaymentConfig(int paymentAmount, int dailyPaymentLimit, int paymentChance, String requiredPermission) {
        this.paymentAmount = paymentAmount;
        this.dailyPaymentLimit = dailyPaymentLimit;
        this.paymentChance = paymentChance;
        this.requiredPermission = requiredPermission;
    }

    @SuppressWarnings("unused")
    public PaymentConfig() {
    }

    public int getPaymentAmount() {
        return Math.max(paymentAmount, 0);
    }

    public int getDailyPaymentLimit() {
        return Math.max(dailyPaymentLimit, 0);
    }

    public int getPaymentChance() {
        return MainConfig.between(paymentChance, 0, 100);
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    static List<PaymentConfig> defaults() {
        return new ArrayList<PaymentConfig>() {{
            add(new PaymentConfig(150, 0, 30, "nico.donator"));
            add(new PaymentConfig(50, 1500, 90, ""));
        }};
    }
}