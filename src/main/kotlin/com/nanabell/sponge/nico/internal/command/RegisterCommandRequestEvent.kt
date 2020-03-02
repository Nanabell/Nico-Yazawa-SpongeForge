package com.nanabell.sponge.nico.internal.command

import com.nanabell.sponge.nico.internal.event.StandardEvent
import org.spongepowered.api.event.cause.Cause
import kotlin.reflect.KClass

class RegisterCommandRequestEvent(val command: AbstractCommand<*, *>, val clazz: KClass<out AbstractCommand<*, *>>, cause: Cause) : StandardEvent(cause) {
    var isRegistered = false
}