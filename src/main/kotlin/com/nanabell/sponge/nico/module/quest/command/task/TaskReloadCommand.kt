package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["reload"])
class TaskReloadCommand : StandardCommand<QuestModule>() {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        taskRegistry.reload()

        source.sendMessage("All Tasks have been reloaded!".green())
        return CommandResult.success()
    }

    override fun getDescription(): String = "Reload all Tasks from the TaskStore"
}