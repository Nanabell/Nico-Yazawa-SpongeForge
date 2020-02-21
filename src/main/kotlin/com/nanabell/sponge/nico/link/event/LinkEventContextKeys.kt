package com.nanabell.sponge.nico.link.event

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import net.dv8tion.jda.api.entities.MessageChannel
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider

object LinkEventContextKeys {

    val DISCORD_USER = createFor<DiscordUser>("DISCORD_USER")
    val MINECRAFT_USER = createFor<MinecraftUser>("MINECRAFT_USER")
    val MESSAGE_CHANNEL = createFor<MessageChannel>("DISCORD_MESSAGE_CHANNEL")

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFor(id: String): EventContextKey<T> {
        return DummyObjectProvider.createFor<EventContextKey<*>>(EventContextKey::class.java, id) as EventContextKey<T>
    }
}