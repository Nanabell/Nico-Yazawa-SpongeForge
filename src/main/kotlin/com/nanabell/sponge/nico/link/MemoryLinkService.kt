package com.nanabell.sponge.nico.link;

import com.google.common.collect.HashBiMap;
import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys;
import com.nanabell.sponge.nico.link.event.LinkRequestEvent;
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent;
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

    private final HashBiMap<Long, UUID> pendingLinks = HashBiMap.create();
    private final HashBiMap<Long, UUID> links = HashBiMap.create();

    public MemoryLinkService(NicoYazawa plugin) {
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Override
    public boolean pendingLink(@NotNull Long discordId) {
        return pendingLinks.containsKey(discordId);
    }

    @Override
    public boolean pendingLink(@NotNull UUID minecraftId) {
        return pendingLinks.containsValue(minecraftId);
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
    public LinkResult confirmLink(@NotNull Long discordId) {
        if (links.containsKey(discordId)) {
            return LinkResult.ALREADY_LINKED;
        }

        if (!pendingLinks.containsKey(discordId)) {
            return LinkResult.NO_LINK_REQUEST;
        }

        links.put(discordId, pendingLinks.get(discordId));
        pendingLinks.remove(discordId);
        return LinkResult.SUCCESS;
    }

    @Override
    public LinkResult confirmLink(@NotNull UUID minecraftId) {
        if (links.containsValue(minecraftId)) {
            return LinkResult.ALREADY_LINKED;
        } else if (!pendingLinks.containsValue(minecraftId)) {
            return LinkResult.NO_LINK_REQUEST;
        }

        links.put(pendingLinks.inverse().get(minecraftId), minecraftId);
        pendingLinks.inverse().remove(minecraftId);
        return LinkResult.SUCCESS;
    }

    @Override
    public LinkResult unlink(@NotNull Long discordId) {
        if (!links.containsKey(discordId)) {
            return LinkResult.NOT_LINKED;
        }

        links.remove(discordId);
        return LinkResult.SUCCESS;
    }

    @Override
    public LinkResult unlink(@NotNull UUID minecraftId) {
        if (!links.containsValue(minecraftId)) {
            return LinkResult.NOT_LINKED;
        }

        links.inverse().remove(minecraftId);
        return LinkResult.SUCCESS;
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

            Sponge.getEventManager().post(new LinkStateChangeEvent(LinkState.BROKEN, Cause.of(linkContext, linkCause)));
            return;
        }

        Player player = oPlayer.get();
        pendingLinks.put(user.getIdLong(), player.getUniqueId());

        Text msg = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.getAsTag() + " "))
                .append(Text.of(TextColors.GREEN, "[Confirm]", TextActions.runCommand("/nico link accept")))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED, "[Deny]", TextActions.runCommand("/nico link deny")))
                .build();

        player.sendMessage(msg);
    }
}
