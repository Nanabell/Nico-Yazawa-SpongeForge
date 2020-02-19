package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import org.spongepowered.api.event.cause.Cause

interface LinkService {

    fun isPending(user: DiscordUser): Boolean
    fun isPending(user: MinecraftUser): Boolean
    fun addPending(discordUser: DiscordUser, minecraftUser: MinecraftUser): Boolean
    fun removePending(user: MinecraftUser): Boolean

    fun isLinked(user: MinecraftUser): Boolean
    fun getLink(user: MinecraftUser): Link?
    fun confirmLink(user: MinecraftUser, cause: Cause): LinkResult
    fun unlink(user: MinecraftUser, cause: Cause): LinkResult

}

