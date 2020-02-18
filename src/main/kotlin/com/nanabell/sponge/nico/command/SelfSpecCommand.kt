package com.nanabell.sponge.nico.command

import org.spongepowered.api.command.spec.CommandSpec

interface SelfSpecCommand {
    fun aliases(): Array<String>
    fun spec(): CommandSpec
}