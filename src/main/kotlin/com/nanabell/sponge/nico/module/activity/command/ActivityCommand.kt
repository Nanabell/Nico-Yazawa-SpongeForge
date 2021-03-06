package com.nanabell.sponge.nico.module.activity.command

import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.activity.ActivityModule

@Permissions
@RegisterCommand(["activity"], hasExecutor = false)
class ActivityCommand : NoExecutorCommand<ActivityModule>() {

    override fun getDescription(): String = ""

}