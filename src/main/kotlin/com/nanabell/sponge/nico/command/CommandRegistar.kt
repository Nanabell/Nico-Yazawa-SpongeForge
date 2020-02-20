package com.nanabell.sponge.nico.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.command.economy.NicoGetCommand
import com.nanabell.sponge.nico.command.economy.NicoSetCommand
import com.nanabell.sponge.nico.command.link.LinkCommand
import com.nanabell.sponge.nico.extensions.orNull
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandManager
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text

class CommandRegistar(private val plugin: NicoYazawa) {

    private val commandManager: CommandManager = Sponge.getCommandManager()
    private val permissionService = Sponge.getServiceManager().provide(PermissionService::class.java).orNull()
    private val nicoCommandBuilder = CommandSpec.builder()

    init {
        loadCommands()
    }

    private fun loadCommands() { // Currency
        addCommand(NicoGetCommand())
        addCommand(NicoSetCommand())
        // Discord-Link
        addCommand(LinkCommand())
        // Finally register the commands to Sponge
        registerCommands()
    }

    private fun addCommand(command: SelfSpecCommand) {
        nicoCommandBuilder.child(command.spec(), *command.aliases())

        if (permissionService != null)
            command.permissionDescriptions(permissionService.newDescriptionBuilder(plugin))
    }

    private fun registerCommands() {
        val nicoCommand = nicoCommandBuilder.description(Text.of("Nico Nico Ni!"))
                .permission("nico")
                .build()

        commandManager.register(plugin, nicoCommand, "nico", "n")
    }
}
