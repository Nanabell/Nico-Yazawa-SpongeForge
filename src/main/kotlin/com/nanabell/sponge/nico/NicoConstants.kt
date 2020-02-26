package com.nanabell.sponge.nico

import com.nanabell.sponge.nico.activity.ActivityPlayer
import com.nanabell.sponge.nico.command.CurrencyElement
import com.nanabell.sponge.nico.command.DiscordUserElement
import com.nanabell.sponge.nico.extensions.DiscordUser
import net.dv8tion.jda.api.entities.MessageChannel
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider

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

    val DISCORD_USER = createFor<DiscordUser>("nico-yazawa:discord-user")
    val DISCORD_CHANNEL = createFor<MessageChannel>("nico-yazawa:discord-channel")
    val COMMAND_SOURCE = createFor<CommandSource>("command-source")
    val INACTIVE = createFor<Long>("nico-yazawa:inactive-since")
    val ACTIVITY_PLAYER = createFor<ActivityPlayer>("nico-yazawa:activity-player")

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFor(id: String): EventContextKey<T> {
        return DummyObjectProvider.createFor<EventContextKey<*>>(EventContextKey::class.java, id) as EventContextKey<T>
    }
}
