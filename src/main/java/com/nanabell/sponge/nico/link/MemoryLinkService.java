package com.nanabell.sponge.nico.link;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys;
import com.nanabell.sponge.nico.link.event.LinkFailedEvent;
import com.nanabell.sponge.nico.link.event.LinkRequestEvent;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class MemoryLinkService implements LinkService {

    private final Logger logger = LoggerFactory.getLogger(MemoryLinkService.class);

    private final Map<Long, UUID> links = new HashMap<>();
    private final Set<Long> pendingLinks = new HashSet<>();

    public MemoryLinkService(NicoYazawa plugin) {
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Override
    public boolean pendingLink(@NotNull Long discordId) {
        return pendingLinks.contains(discordId);
    }

    @Override
    public boolean isLinked(@NotNull Long discordId) {
        return links.containsKey(discordId);
    }

    @Override
    public boolean isLinked(@NotNull UUID minecraftId) {
        return links.containsValue(minecraftId);
    }

    @Override
    public LinkResult link(@NotNull Long discordId, @NotNull UUID minecraftId) {
        if (links.containsKey(discordId)) {
            return LinkResult.DISCORD_ALREADY_LINKED;
        } else if (links.containsValue(minecraftId)) {
            return LinkResult.MINECRAFT_ALREADY_LINKED;
        }

        links.put(discordId, minecraftId);
        return LinkResult.SUCCESS;
    }

    @Override
    public LinkResult unlink(@NotNull Long discordId) {
        if (links.containsKey(discordId)) {
            return LinkResult.NOT_LINKED;
        }

        links.remove(discordId);
        return LinkResult.SUCCESS;
    }

    @Override
    public LinkResult unlink(@NotNull UUID minecraftId) {
        Optional<Long> oDiscordId = links.entrySet().stream()
                .filter(entry -> entry.getValue().equals(minecraftId))
                .map(Map.Entry::getKey)
                .findFirst();

        return oDiscordId.isPresent() ? unlink(oDiscordId.get()) : LinkResult.NOT_LINKED;
    }

    @Listener
    public void onLinkRequest(LinkRequestEvent event) {
        EventContext context = event.getCause().getContext();

        Optional<User> oUser = context.get(LinkEventContextKeys.USER);
        if (!oUser.isPresent()) {
            logger.warn("OnLinkRequest did not include a context User." + event);
            return;
        }

        User user = oUser.get();
        if (pendingLink(user.getIdLong())) {
            logger.warn("Received LinkRequestEvent for user who already has a pending Request" + event);
            return; // Already Pending Link
        }

        Optional<Player> oPlayer = Sponge.getServer().getPlayer(event.getTargetUserName());
        if (!oPlayer.isPresent()) {
            EventContext linkContext = EventContext.builder().add(LinkEventContextKeys.LINK_RESULT, LinkResult.USER_NOT_FOUND).build();
            Cause linkCause = Cause.builder().from(event.getCause()).append(this).build(linkContext);

            Sponge.getEventManager().post(new LinkFailedEvent(Cause.of(linkContext, linkCause)));
            return;
        }

        pendingLinks.add(user.getIdLong());

        Player player = oPlayer.get();
        Text msg = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.getAsTag() + " "))
                .append(Text.of(TextColors.GREEN, "[Confirm]", TextActions.runCommand("/nico link accept")))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED, "[Deny]", TextActions.runCommand("/nico link deny")))
                .build();

        player.sendMessage(msg);
    }
}
