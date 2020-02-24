package com.nanabell.sponge.nico.internal.command

import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandPermissionException
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.function.Consumer
import java.util.stream.Collectors

class NicoCommandException(
        private val exceptions: List<Pair<String, CommandException>>,
        val allowFallback: Boolean
) : CommandException(Text.EMPTY) {

    constructor(exceptions: List<Pair<String, CommandException>>): this(exceptions, true)

    override fun getText(): Text? {
        if (exceptions.isEmpty()) { // Unable to get the error.
            return "Command Failed with unknown Reason".toText()
        }

        // Is it only command permission exceptions?
        if (exceptions.stream().allMatch { pair: Pair<String, CommandException> -> CommandPermissionException::class.java.isInstance(pair.second) }) {
            return exceptions[0].second.text
        }

        if (exceptions.stream().allMatch { pair: Pair<String, CommandException> -> pair.second is NicoArgumentParseException && (pair.second as NicoArgumentParseException).isEnd }) {
            return if (exceptions.size == 1) {
                return Text.of("ArgumentParseException: ${exceptions[0].first}".toText(), Text.NEW_LINE, TextColors.RED, exceptions[0].second.text)
            } else {
                print(exceptions)
            }
        }

        val lce: List<Pair<String, CommandException>> = exceptions.stream()
                .filter { pair: Pair<String, CommandException> -> pair.second !is NicoArgumentParseException || !(pair.second as NicoArgumentParseException).isEnd }
                .filter { pair: Pair<String, CommandException> -> !CommandPermissionException::class.java.isInstance(pair.second) }
                .collect(Collectors.toList())

        if (lce.size == 1) {
            return Text.of("Command Exception: ${exceptions[0].first}".toText(), Text.NEW_LINE, TextColors.RED, exceptions[0].second.text)
        }

        return print(lce)
    }

    private fun print(exceptions: List<Pair<String, CommandException>>): Text {
        val sept: Text = ", ".toText()
        val builder: Text.Builder = "Multiple Exceptions: ".toText().toBuilder()

        exceptions.forEach(Consumer { x: Pair<String, CommandException> ->
            builder.append(Text.NEW_LINE).append(sept)
                    .append(Text.NEW_LINE)
                    .append("Command Exception: ${x.first}".toText())
                    .append(Text.NEW_LINE)
                    .append(x.second.text)
        })

        builder.append(Text.NEW_LINE).append(sept)
        return builder.toText()
    }
}