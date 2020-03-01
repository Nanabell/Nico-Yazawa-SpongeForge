package com.nanabell.sponge.nico.internal.extension

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun Instant.formatDefault(): String {
    return Formatters.instantFormatter.format(this)
}

fun Duration.formatDefault(): String {
    return this.toString()
            .substring(2)
            .replace("(\\d[HMS])(?!$)".toRegex(), "$1 ")
            .replace("(\\.\\d+)S$".toRegex(), "S")
            .toLowerCase()
}

object Formatters {

    val instantFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())

}
