package com.nanabell.sponge.nico.command

import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text

class CurrencyElement(key: Text) : CommandElement(key) {

    private val economy = Sponge.getServiceManager().provideUnchecked(EconomyService::class.java)

    override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
        if (!args.hasNext()) throw args.createError("Missing Currency Argument!".toText())

        return economy.currencies.firstOrNull {
            it.name.replace(' ', '_') == args.next()
        }
    }

    override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): MutableList<String> {
        val state = args.snapshot
        val nextArg = args.nextIfPresent().orNull()
        args.applySnapshot(state)

         return if (nextArg != null) economy.currencies.map { it.name.replace(' ', '_') }.toMutableList() else mutableListOf()
    }

}
