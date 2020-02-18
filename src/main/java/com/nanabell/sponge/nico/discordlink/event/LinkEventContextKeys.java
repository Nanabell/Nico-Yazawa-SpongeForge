package com.nanabell.sponge.nico.discordlink.event;

import com.nanabell.sponge.nico.discordlink.LinkResult;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public class LinkEventContextKeys {

    public static final EventContextKey<User> USER = createFor("DISCORD_USER");
    public static final EventContextKey<MessageChannel> MESSAGE_CHANNEL = createFor("DISCORD_MESSAGE_CHANNEL");
    public static final EventContextKey<LinkResult> LINK_RESULT = createFor("Nico-Yazawa:LINK_RESULT");


    @SuppressWarnings("unchecked")
    private static <T> EventContextKey<T> createFor(String id) {
        return DummyObjectProvider.createFor(EventContextKey.class, id);
    }

}
