package com.nanabell.sponge.nico.argument

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.text.Text
import java.util.*

class TaskArgument(key: Text) : CommandElement(key) {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
        if (!args.hasNext()) throw args.createError("Missing Task ID Argument!".toText())
        val id = args.next()

        try {
            return taskRegistry.get(UUID.fromString(id))
        } catch (e: IllegalArgumentException) {
            throw args.createError("$id is not a valid UUID!".red())
        }
    }

    override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): List<String> {
        if (!args.hasNext()) return mutableListOf()
        val arg = args.peek()

        return taskRegistry.getAll().filter { !it.isAttached() }.map { it.id.toString() }.filter { it.startsWith(arg) }
    }
}