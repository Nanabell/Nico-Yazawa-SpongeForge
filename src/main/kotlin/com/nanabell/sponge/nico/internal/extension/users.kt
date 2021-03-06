package com.nanabell.sponge.nico.internal.extension

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import net.dv8tion.jda.api.entities.Member
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

private val userCache by lazy { Sponge.getServiceManager().provideUnchecked(UserStorageService::class.java) }
private val discordService: DiscordService? by lazy { NicoYazawa.getServiceRegistry().provide<DiscordService>() }

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

fun Long.toDiscordUser(): DiscordUser? {
    return discordService?.getUser(this)
}

fun Long.toDiscordMember(): Member? {
    return discordService?.getMember(this)
}

