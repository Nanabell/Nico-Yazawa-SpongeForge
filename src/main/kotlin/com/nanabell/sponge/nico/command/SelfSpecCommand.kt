package com.nanabell.sponge.nico.command

import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionDescription

interface SelfSpecCommand {
    fun aliases(): Array<String>
    fun spec(): CommandSpec
    fun permissionDescriptions(builder: PermissionDescription.Builder)
}