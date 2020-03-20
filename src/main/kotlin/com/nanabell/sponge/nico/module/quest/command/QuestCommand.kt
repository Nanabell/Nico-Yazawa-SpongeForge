package com.nanabell.sponge.nico.module.quest.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.module.quest.QuestModule
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.event.cause.Cause

@RegisterCommand(["quest"])
class QuestCommand : StandardCommand<QuestModule>() {

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        TODO("Not yet implemented")
    }

    override fun getDescription(): String = ""

}
