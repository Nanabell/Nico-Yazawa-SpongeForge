package com.nanabell.sponge.nico.internal.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterService(
        val value: KClass<*>,
        val override: Boolean = false
)