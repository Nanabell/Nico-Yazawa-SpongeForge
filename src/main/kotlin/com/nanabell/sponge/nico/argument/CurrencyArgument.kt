package com.nanabell.sponge.nico.argument

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.toText
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text

class CurrencyArgument(key: Text) : CommandElement(key) {

    private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
        if (!args.hasNext()) throw args.createError("Missing Currency Argument!".toText())

        return economy.currencies.firstOrNull {
            it.name.replace(' ', '_') == args.peek()
        }.also { args.next() }
    }

    override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): MutableList<String> {
        if (!args.hasNext()) return mutableListOf()
        val arg = args.peek()

        return economy.currencies.map { it.name.replace(' ', '_') }.filter { it.startsWith(arg) }.toMutableList()
    }

}
