package com.nanabell.sponge.nico.link.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class LinkRequestEvent extends LinkEvent implements Event {

    private String targetUser;

    public LinkRequestEvent(String targetUser, Cause cause) {
        super(cause);

        this.targetUser = targetUser;
    }

    public String getTargetUserName() {
        return targetUser;
    }
}
