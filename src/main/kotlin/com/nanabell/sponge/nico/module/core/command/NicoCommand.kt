package com.nanabell.sponge.nico.module.core.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.core.CoreModule

@RegisterCommand(["nico"], hasExecutor = false)
class NicoCommand : NoExecutorCommand<CoreModule>() {

    override fun getDescription(): String = "Base Nico Command"

}