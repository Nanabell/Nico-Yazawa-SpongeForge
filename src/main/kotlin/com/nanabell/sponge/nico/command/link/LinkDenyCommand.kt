package com.nanabell.sponge.nico.command.link

import com.nanabell.sponge.nico.command.SelfSpecCommand
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text

class LinkDenyCommand : CommandExecutor, SelfSpecCommand {
    override fun aliases(): Array<String> {
        return arrayOf("deny", "d")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Deny a pending Discord-Link request"))
                .executor(this)
                .build()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        return CommandResult.success()
    }
}