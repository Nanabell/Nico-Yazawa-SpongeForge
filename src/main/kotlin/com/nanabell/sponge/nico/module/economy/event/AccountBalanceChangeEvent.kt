package com.nanabell.sponge.nico.module.economy.event

import com.nanabell.sponge.nico.internal.event.StandardCancellableEvent
import org.spongepowered.api.event.cause.Cause
import java.math.BigDecimal

class AccountBalanceChangeEvent(val from: BigDecimal, val to: BigDecimal, cause: Cause) : StandardCancellableEvent(cause)