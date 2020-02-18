package com.nanabell.sponge.nico.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class LinkEvent extends AbstractEvent {

    private Cause cause;

    public LinkEvent(Cause cause) {
        this.cause = cause;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
