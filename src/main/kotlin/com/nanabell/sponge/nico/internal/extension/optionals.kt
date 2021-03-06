package com.nanabell.sponge.nico.internal.extension

import java.util.*

fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)
fun <T> Optional<T>.orNull(): T? = orElse(null)