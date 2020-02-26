package com.nanabell.sponge.nico.command

import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.text.Text
import java.util.regex.Pattern

class DiscordUserElement(key: Text) : CommandElement(key) {

    private val discordService by lazy { Sponge.getServiceManager().provideUnchecked(DiscordService::class.java) }

    private val tagPattern = Pattern.compile("^.{2,32}#\\d{4}$")

    override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
        if (!args.hasNext()) throw args.createError("Missing Currency Argument!".toText())

        val name = args.next()
        return when {
            tagPattern.matcher(name).find() -> discordService.getUser(name)
            name.toLongOrNull() != null -> discordService.getUser(name.toLong())
            else -> null
        }
    }

    override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): MutableList<String> {
        if (!args.hasNext())
            return mutableListOf()

        val arg = args.peek()
        val choices = mutableListOf<String>()
        for (user in discordService.getUserCache()) {
            if (user.asTag.startsWith(arg))
                choices.add(user.asTag)

            if (user.id.startsWith(arg))
                choices.add(user.id)
        }

        return choices
    }
}