package com.nanabell.sponge.nico.module.quest.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.quest.QuestModule

@RegisterCommand(["task"], hasExecutor = false)
class TaskCommand : NoExecutorCommand<QuestModule>() {

    override fun getDescription(): String = "Base Quest Command"
}