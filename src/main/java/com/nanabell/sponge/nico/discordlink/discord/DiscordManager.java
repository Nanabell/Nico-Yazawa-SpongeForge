package com.nanabell.sponge.nico.discordlink.discord;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.config.Config;
import com.nanabell.sponge.nico.config.MainConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private final NicoYazawa plugin;
    private final Config<MainConfig> configManager;
    private final Logger logger;

    private JDA jda;

    public DiscordManager(NicoYazawa plugin) {
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
