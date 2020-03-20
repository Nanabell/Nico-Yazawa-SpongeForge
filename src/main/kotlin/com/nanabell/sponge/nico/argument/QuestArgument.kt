package com.nanabell.sponge.nico.argument

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.text.Text
import java.util.*

class QuestArgument(key: Text) : CommandElement(key) {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
        if (!args.hasNext()) throw args.createError("Missing Quest ID Argument!".toText())
        val id = args.next()

        try {
            return questRegistry.get(UUID.fromString(id))
        } catch (e: IllegalArgumentException) {
            throw args.createError("$id is not a valid UUID!".red())
        }

    }

    override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): List<String> {
        if (!args.hasNext()) return mutableListOf()
        val arg = args.peek()

        return questRegistry.getAll().map { it.id.toString() }.filter { it.startsWith(arg) }
    }

}
