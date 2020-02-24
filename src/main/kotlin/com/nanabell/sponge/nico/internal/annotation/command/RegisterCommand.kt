package com.nanabell.sponge.nico.internal.annotation.command

import com.nanabell.sponge.nico.internal.command.AbstractCommand
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterCommand(
        val value: Array<String>,
        val subCommandOf: KClass<out AbstractCommand<*>> = AbstractCommand::class,
        val hasExecutor: Boolean = true
)