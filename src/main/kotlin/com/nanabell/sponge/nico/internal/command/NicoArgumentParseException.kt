package com.nanabell.sponge.nico.internal.command

import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.command.args.ArgumentParseException
import org.spongepowered.api.text.Text

class NicoArgumentParseException(
        message: Text,
        source: String,
        position: Int,
        private val usage: Text?,
        private val subcommands: Text?,
        val isEnd: Boolean
) : ArgumentParseException(message, source, position) {

    override fun getText(): Text? {
        val t = super.getText()
        return if (this.usage == null && subcommands == null) {
            t
        } else Text.join(t, Text.NEW_LINE, getUsage())
    }

    fun getUsage(): Text? {
        val builder = Text.builder()
        if (usage != null) {
            builder.append(Text.NEW_LINE).append("Usage: ".toText()).append(usage)
        }
        if (subcommands != null) {
            builder.append(Text.NEW_LINE).append("SubCommands: ".toText()).append(subcommands)
        }

        return builder.build()
    }
}