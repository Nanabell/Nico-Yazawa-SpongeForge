package com.nanabell.nico.nicoscoffee.discordlink.discord;

import com.nanabell.nico.nicoscoffee.NicosCoffee;
import com.nanabell.nico.nicoscoffee.config.Config;
import com.nanabell.nico.nicoscoffee.config.MainConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private final NicosCoffee plugin;
    private final Config<MainConfig> configManager;
    private final Logger logger;

    private JDA jda;

    public DiscordManager(NicosCoffee plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.logger = plugin.getLogger();

        try {
            jda = new JDABuilder(configManager.get().getDiscordLinkConfig().getToken()).build().awaitReady();
        } catch (InterruptedException e) {
            logger.error("Error connecting to discord", e);
        } catch (LoginException e) {
            logger.error("Failed to Log into Discord! Please Provide a valid Token!", e);
        }
    }
}
