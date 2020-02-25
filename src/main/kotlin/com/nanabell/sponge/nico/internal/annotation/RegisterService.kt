package com.nanabell.sponge.nico.internal.annotation

import com.nanabell.sponge.nico.internal.service.AbstractService
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterService(
        val value: KClass<in AbstractService>,
        val override: Boolean = false
)