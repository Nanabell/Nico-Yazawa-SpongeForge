package com.nanabell.sponge.nico.module.economy.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.economy.EconomyModule

@RegisterCommand(["points"], hasExecutor = false)
class PointsCommand : NoExecutorCommand<EconomyModule>() {

    override fun getDescription(): String = "Base Command for Nico & Maki Points"

}