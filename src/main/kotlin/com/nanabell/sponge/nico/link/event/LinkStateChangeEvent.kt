package com.nanabell.sponge.nico.event;

import com.nanabell.sponge.nico.link.LinkState;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class LinkStateChangeEvent extends LinkEvent implements Event {

    private final LinkState state;

    public LinkStateChangeEvent(LinkState state, Cause cause) {
        super(cause);

        this.state = state;
    }

    public LinkState getState() {
        return state;
    }
}
