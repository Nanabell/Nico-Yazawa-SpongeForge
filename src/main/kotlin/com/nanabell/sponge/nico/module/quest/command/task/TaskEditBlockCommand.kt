package com.nanabell.sponge.nico.module.quest.command.task

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.task.MineBlockTask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import org.spongepowered.api.CatalogTypes
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["block"], TaskEditCommand::class)
class TaskEditBlockCommand : StandardCommand<QuestModule>() {

    private val taskRegistry: TaskRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.task("task".toText(), true),
                GenericArguments.optional(
                        GenericArguments.catalogedElement("block".toText(), CatalogTypes.BLOCK_TYPE)
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val task = args.requireOne<ITask>("task")
        val blockType = args.getOne<BlockType>("block").orNull()

        when (task) {
            is MineBlockTask -> task.target = blockType?.id
            else -> throw CommandException("Task $task does not have a Block property!".red())
        }

        taskRegistry.set(task)
        return Sponge.getCommandManager().process(source, "task edit ${task.id}")
    }

    override fun getDescription(): String = "Edit a Tasks Block Property"
}