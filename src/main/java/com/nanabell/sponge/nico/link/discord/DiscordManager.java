package com.nanabell.sponge.nico.link.discord;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.config.Config;
import com.nanabell.sponge.nico.config.DiscordLinkConfig;
import com.nanabell.sponge.nico.config.MainConfig;
import com.nanabell.sponge.nico.link.LinkService;
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys;
import com.nanabell.sponge.nico.link.event.LinkRequestEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DiscordManager extends ListenerAdapter {

    private final LinkService linkService;
    private final Config<MainConfig> configManager;
    private final Logger logger;

    private JDA jda;
    private Set<Long> pendingUsername = new HashSet<>();

    public DiscordManager(NicoYazawa plugin) {
        this.linkService = plugin.getServiceManager().provideUnchecked(LinkService.class);
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
        User user = event.getUser();
        if (user.isBot() || user.isFake()) return;

        DiscordLinkConfig config = configManager.get().getDiscordLinkConfig();

        if (event.getMessageIdLong() == config.getMessageId()) {
            if (event.getReactionEmote().getAsCodepoints().equals(config.getReactionEmote())) {
                if (!linkService.pendingLink(user.getIdLong())) {
                    pendingUsername.add(user.getIdLong());

                    user.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("What is you Minecraft username?").queue();
                    });

                }
            } else {
                event.getReaction().removeReaction(user).queueAfter(1, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot() || user.isFake()) return;

        if (pendingUsername.contains(user.getIdLong())) {
            pendingUsername.remove(user.getIdLong());

            String username = event.getMessage().getContentRaw().split(" ")[0];
            EventContext eventContext = EventContext.builder()
                    .add(LinkEventContextKeys.MESSAGE_CHANNEL, event.getChannel())
                    .add(LinkEventContextKeys.USER, event.getAuthor())
                    .build();

            Sponge.getEventManager().post(new LinkRequestEvent(username, Cause.of(eventContext, this)));
        }
    }
}
