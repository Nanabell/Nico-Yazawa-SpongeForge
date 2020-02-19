package com.nanabell.sponge.nico.link.event

import com.nanabell.sponge.nico.link.LinkResult
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider

object LinkEventContextKeys {
    val USER = createFor<User>("DISCORD_USER")
    val MESSAGE_CHANNEL = createFor<MessageChannel>("DISCORD_MESSAGE_CHANNEL")
    val LINK_RESULT = createFor<LinkResult>("Nico-Yazawa:LINK_RESULT")

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFor(id: String): EventContextKey<T> {
        return DummyObjectProvider.createFor<EventContextKey<*>>(EventContextKey::class.java, id) as EventContextKey<T>
    }
}