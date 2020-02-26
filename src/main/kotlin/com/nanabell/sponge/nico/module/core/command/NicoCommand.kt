package com.nanabell.sponge.nico.module.core.command

import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.module.core.CoreModule
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["nico"], hasExecutor = false)
class NicoCommand : StandardCommand<CoreModule>() {

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        throw IllegalStateException("This Command cannot be executed!")
    }

    override fun getDescription(): String = "Base Nico Command"

}