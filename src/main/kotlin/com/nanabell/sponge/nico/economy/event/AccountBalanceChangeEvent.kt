package com.nanabell.sponge.nico.economy.event

import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause
import java.math.BigDecimal

class AccountBalanceChangeEvent(val from: BigDecimal, val to: BigDecimal, private val cause: Cause) : Event, Cancellable {

    private var canceled = false

    override fun getCause(): Cause = cause

    override fun isCancelled(): Boolean = canceled
    override fun setCancelled(cancel: Boolean) {
        canceled = cancel
    }
}