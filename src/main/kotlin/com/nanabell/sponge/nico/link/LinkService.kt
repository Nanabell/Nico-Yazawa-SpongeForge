package com.nanabell.sponge.nico.link

typealias DiscordUser = net.dv8tion.jda.api.entities.User
typealias MinecraftUser = org.spongepowered.api.entity.living.player.User

interface LinkService {
    fun pendingLink(user: DiscordUser): Boolean
    fun pendingLink(user: MinecraftUser): Boolean
    fun isLinked(user: DiscordUser): Boolean
    fun isLinked(user: MinecraftUser): Boolean
    fun confirmLink(user: DiscordUser): LinkResult?
    fun confirmLink(user: MinecraftUser): LinkResult?
    fun unlink(user: DiscordUser): LinkResult?
    fun unlink(user: MinecraftUser): LinkResult?
}

