package com.nanabell.sponge.nico.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.command.economy.NicoGetCommand
import com.nanabell.sponge.nico.command.economy.NicoSetCommand
import com.nanabell.sponge.nico.command.link.LinkAcceptCommand
import com.nanabell.sponge.nico.command.link.LinkCommand
import com.nanabell.sponge.nico.command.link.LinkDenyCommand
import com.nanabell.sponge.nico.command.link.UnlinkCommand
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandManager
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text

class CommandRegistar(private val plugin: NicoYazawa) {

    private val commandManager: CommandManager = Sponge.getCommandManager()
    private val permissionService = Sponge.getServiceManager().provide(PermissionService::class.java).orNull()
    private val nicoCommandBuilder = CommandSpec.builder()

    init {
        loadCommands()
    }

    fun loadCommands() {
        // Admin
        addCommand(DummyCommand())

        // Currency
        addCommand(NicoGetCommand())
        addCommand(NicoSetCommand())

        // Discord-Link
        addCommand(LinkAcceptCommand())
        addCommand(LinkDenyCommand())
        addCommand(LinkCommand())
        addCommand(UnlinkCommand())

        // Finally register the commands to Sponge
        registerCommands()
    }

    private fun addCommand(command: SelfSpecCommand) {
        nicoCommandBuilder.child(command.spec(), *command.aliases())

        if (permissionService != null)
            command.permissionDescriptions(permissionService.newDescriptionBuilder(plugin))
    }

    private fun registerCommands() {
        val nicoCommand = nicoCommandBuilder.description(Text.of("Nico Nico Ni!")).build()

        commandManager.register(plugin, nicoCommand, "nico", "n")
    }
}

fun CommandSource.requirePlayerOrArg(args: CommandContext, playerArg: String): Player {
    if (!args.hasAny(playerArg.toText()) && this !is Player)
        throw CommandException(Text.of("Cannot Target " + this.name + ". Valid Target is [Player]"))

    return if (this is Player) this else args.requireOne(playerArg)
}

typealias Args = GenericArguments
