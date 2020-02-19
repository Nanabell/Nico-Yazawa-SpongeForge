package com.nanabell.sponge.nico.link.event

import com.nanabell.sponge.nico.link.LinkState
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

class LinkStateChangeEvent(val state: LinkState, cause: Cause) : LinkEvent(cause), Event