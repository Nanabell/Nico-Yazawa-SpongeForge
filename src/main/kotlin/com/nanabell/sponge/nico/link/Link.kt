package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.extensions.orNull
import net.dv8tion.jda.api.JDA
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

class Link(val discordId: Long, val minecraftId: UUID) {

    fun fetchUser(jda: JDA): DiscordUser? {
        return jda.getUserById(discordId)
    }

    fun fetchUser(userStorage: UserStorageService): MinecraftUser? {
        return userStorage[minecraftId].orNull()
    }
}