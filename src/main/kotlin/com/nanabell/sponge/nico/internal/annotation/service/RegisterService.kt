package com.nanabell.sponge.nico.internal.annotation.service

import com.nanabell.sponge.nico.internal.service.AbstractService
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterService(
        val value: KClass<*> = AbstractService::class,
        val override: Boolean = false
)