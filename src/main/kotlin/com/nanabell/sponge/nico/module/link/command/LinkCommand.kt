package com.nanabell.sponge.nico.module.link.command

import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.NoExecutorCommand
import com.nanabell.sponge.nico.module.link.LinkModule

@RegisterCommand(["link"], hasExecutor = false)
class LinkCommand : NoExecutorCommand<LinkModule>() {

    override fun getDescription(): String = "Command related to Minecraft <--> Discord Link"

}
