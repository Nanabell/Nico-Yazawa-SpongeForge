package com.nanabell.sponge.nico.command

import com.nanabell.sponge.nico.discord.DiscordService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.PatternMatchingCommandElement
import org.spongepowered.api.text.Text
import java.util.regex.Pattern

class DiscordUserElement(key: Text) : PatternMatchingCommandElement(key) {

    private val discordService by lazy { Sponge.getServiceManager().provideUnchecked(DiscordService::class.java) }

    private val tagPattern = Pattern.compile("^.{2,32}#\\d{4}$")

    override fun getChoices(source: CommandSource): MutableIterable<String> {
        val choices = mutableListOf<String>()
        for (user in discordService.jda.userCache) {
            choices.add(user.asTag)
            choices.add(user.id)
        }

        return choices
    }

    override fun getValue(choice: String): Any? {
        return when {
            tagPattern.matcher(choice).find() -> discordService.jda.getUserByTag(choice)
            choice.toLongOrNull() != null -> discordService.getUserById(choice)
            else -> choice
        }
    }
}