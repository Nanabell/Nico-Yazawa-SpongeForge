package com.nanabell.sponge.nico.link.event

import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

class LinkRequestEvent(val target: String, cause: Cause) : LinkEvent(cause), Event