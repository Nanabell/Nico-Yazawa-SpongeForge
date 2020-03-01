package com.nanabell.sponge.nico.module.activity.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.activity.ActivityModule

@RegisterCommand(["reward"], ActivityCommand::class , false)
class RewardCommand : NoExecutorCommand<ActivityModule>() {

    override fun getDescription(): String = "" // TODO: Add Description

}
