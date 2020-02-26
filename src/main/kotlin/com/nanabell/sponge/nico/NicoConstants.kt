package com.nanabell.sponge.nico

import com.nanabell.sponge.nico.command.CurrencyElement
import com.nanabell.sponge.nico.command.DiscordUserElement
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider
import java.time.Duration

object NicoConstants {

    const val ID = "nico-yazawa"
    const val NAME = "@name@"
    const val VERSION = "@version@"
    const val DESCRIPTION = "@description@"
    const val PERMISSION_PREFIX = "nico."

    val SPACE: Text = Text.of(" ")


    // CommandElement
    fun currency(currency: Text): CommandElement = CurrencyElement(currency)
    fun discordUser(user: Text): CommandElement = DiscordUserElement(user)

    // EventContextKeys
    val COMMAND_SOURCE = createFor<CommandSource>("command-source")
    val INACTIVE = createFor<Duration>("nico-yazawa:inactive-since")

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFor(id: String): EventContextKey<T> {
        return DummyObjectProvider.createFor<EventContextKey<*>>(EventContextKey::class.java, id) as EventContextKey<T>
    }
}
