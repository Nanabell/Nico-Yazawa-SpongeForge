package com.nanabell.sponge.nico.extensions

import org.spongepowered.api.Sponge
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

private val userCache by lazy { Sponge.getServiceManager().provideUnchecked(UserStorageService::class.java) }

fun UUID.toMinecraftUser(): MinecraftUser? {
    var user = Sponge.getServer().getPlayer(this).orNull() as MinecraftUser?
    if (user == null) {
        user = userCache[this].orNull()
    }

    return user
}

fun String.toMinecraftUser(): MinecraftUser? {
    var user = Sponge.getServer().getPlayer(this).orNull() as MinecraftUser?
    if (user == null) {
        user = userCache[this].orNull()
    }

    return user
}