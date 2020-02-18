package com.nanabell.sponge.nico.discordlink.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class LinkSuccessEvent extends LinkEvent implements Event {

    public LinkSuccessEvent(Cause cause) {
        super(cause);
    }

}
