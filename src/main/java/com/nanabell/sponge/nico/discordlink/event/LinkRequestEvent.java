package com.nanabell.sponge.nico.discordlink.event;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class LinkRequestEvent extends AbstractEvent implements Event {

    public LinkRequestEvent()

    @Override
    public Cause getCause() {
        return null;
    }
}
