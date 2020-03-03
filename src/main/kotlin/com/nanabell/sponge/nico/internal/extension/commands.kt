package com.nanabell.sponge.nico.internal.extension

import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

fun CommandSource.requirePlayerOrArg(args: CommandContext, playerArg: String): Player {
    if (!args.hasAny(playerArg.toText()) && this !is Player)
        throw CommandException("Cannot Target ${this.name}. Valid Target is [Player]".toText())

    return args.getOne<Player>(playerArg).orNull() ?: this as Player
}