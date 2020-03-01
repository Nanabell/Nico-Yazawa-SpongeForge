package com.nanabell.sponge.nico.internal.extension

import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference

fun Subject.getOption(key: String, default: Any): String {
    return this.getOption(key).orNull() ?: default.toString()
}

fun Subject.getOptionToLong(key: String, default: Any): Long? {
    return getOption(key, default).toLongOrNull()
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