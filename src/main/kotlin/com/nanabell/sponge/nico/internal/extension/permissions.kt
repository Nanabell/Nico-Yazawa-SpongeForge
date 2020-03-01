package com.nanabell.sponge.nico.internal.extension

import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import java.time.Duration
import java.time.temporal.ChronoUnit

fun Subject.getOptionDuration(key: String, default: Duration): Duration {
    val seconds = this.getOption(key).orNull()?.toLongOrNull()

    return if (seconds != null) Duration.of(seconds, ChronoUnit.SECONDS) else default
}

fun SubjectData.findParent(identifier: String, contexts: Set<Context> = emptySet()): SubjectReference? {
    return this.allParents[contexts]?.firstOrNull { it.subjectIdentifier == identifier }
}

fun SubjectData.hasParent(identifier: String, contexts: Set<Context> = emptySet()): Boolean {
    return findParent(identifier, contexts) != null
}

fun SubjectCollection.hasIdentifier(identifier: String): Boolean {
    return this.allIdentifiers.get().contains(identifier)
}