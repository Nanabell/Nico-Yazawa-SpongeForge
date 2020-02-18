package com.nanabell.sponge.nico.discordlink.discord;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.config.Config;
import com.nanabell.sponge.nico.config.DiscordLinkConfig;
import com.nanabell.sponge.nico.config.MainConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DiscordManager extends ListenerAdapter {

    private final NicoYazawa plugin;
    private final Config<MainConfig> configManager;
    private final Logger logger;

    private JDA jda;
    private Set<Long> pendingUsername = new HashSet<>();

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

        if (jda != null) {
            jda.addEventListener(this);
        }
    }

    @Override
    public void onGenericGuildMessageReaction(@Nonnull GenericGuildMessageReactionEvent event) {
        User user =  event.getUser();
        if (user.isBot() || user.isFake()) return;

        DiscordLinkConfig config = configManager.get().getDiscordLinkConfig();

        if (event.getMessageIdLong() == config.getMessageId()) {
            if (event.getReactionEmote().getAsCodepoints().equals(config.getReactionEmote())) {
                if (!pendingUsername.contains(user.getIdLong())) {

                }
            } else {
                event.getReaction().removeReaction(user).queueAfter(1, TimeUnit.SECONDS);
            }
        }
    }
}
