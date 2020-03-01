package com.nanabell.sponge.nico.module.info.command

import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.info.InfoModule

@Permissions
@RegisterCommand(["nico"], hasExecutor = false)
class NicoCommand : NoExecutorCommand<InfoModule>() {

    override fun getDescription(): String = "Base Nico Command"

}