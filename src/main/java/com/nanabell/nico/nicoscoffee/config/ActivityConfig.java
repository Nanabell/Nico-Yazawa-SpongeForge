package com.nanabell.nico.nicoscoffee.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ActivityConfig {

    public ActivityConfig() {
        this.paymentConfigs = PaymentConfig.defaults();
        this.disabledWorlds.add("creative_world");
    }

    @Setting(value = "disabled_worlds", comment = "List of Worlds where payments are disabled")
    private List<String> disabledWorlds = new ArrayList<>();

    @Setting(value = "payment_interval", comment = "Interval in Seconds when payments occur [15 = min, 2,147,483,647 = max]")
    private int paymentInterval = 600;

    @Setting(value = "payments", comment = "Payment Settings. Can have multiple. If multiple match only the first match will be used. You can use the chance setting for some randomness")
    private List<PaymentConfig> paymentConfigs;

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public int getPaymentInterval() {
        return Math.max(paymentInterval, 15);
    }

    public List<PaymentConfig> getPaymentConfigs() {
        return paymentConfigs;
    }

}
