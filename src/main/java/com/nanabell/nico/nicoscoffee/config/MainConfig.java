package com.nanabell.nico.nicoscoffee.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MainConfig {

    @Setting(comment = "Database URL used by Nico to store immediate data")
    private String databaseUrl = "jdbc:sqlite:nicos-office.db";

    @Setting(value = "activity", comment = "Nico Points Activity Settings. Gain NicoPoints by being active in Minecraft")
    private ActivityConfig activityConfig = new ActivityConfig();

    @Setting(value = "discord", comment = "Discord Linking Settings. \"Authenticate\" Minecraft users with Discord")
    private DiscordLinkConfig discordLinkConfig = new DiscordLinkConfig();

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public ActivityConfig getActivityConfig() {
        return activityConfig;
    }

    public DiscordLinkConfig getDiscordLinkConfig() {
        return discordLinkConfig;
    }

    static int between(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }
}
