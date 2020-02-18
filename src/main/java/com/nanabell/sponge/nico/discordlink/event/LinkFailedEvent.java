package com.nanabell.sponge.nico.discordlink.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class LinkFailedEvent extends LinkEvent implements Event {

    public LinkFailedEvent(Cause cause) {
        super(cause);
    }
}
