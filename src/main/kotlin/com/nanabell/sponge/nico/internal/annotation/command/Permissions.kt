package com.nanabell.sponge.nico.internal.annotation.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permissions(
        val mainOverride: String = "",
        val prefix: String = "",
        val suffix: String = "",
        val supportsOthers: Boolean = false
)