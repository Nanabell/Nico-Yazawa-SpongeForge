package com.nanabell.sponge.nico.module.core.command

import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.module.core.CoreModule
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["dummy"])
class DummyCommand : AbstractCommand<CommandSource, CoreModule>() {

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        logger.info("Hello World!")
        return CommandResult.success()
    }

    override fun getDescription(): String {
        return "Admin Dummy Command. be careful to use this. This might literally do anything!!"
    }
}