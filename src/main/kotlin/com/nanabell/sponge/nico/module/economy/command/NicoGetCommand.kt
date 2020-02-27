package com.nanabell.sponge.nico.module.economy.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.requirePlayerOrArg
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.economy.EconomyModule
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text

@Permissions(supportsOthers = true)
@RegisterCommand(["get"], PointsCommand::class)
class NicoGetCommand : StandardCommand<EconomyModule>() {

    private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.optional(
                        GenericArguments.onlyOne(
                                GenericArguments.requiringPermission(
                                        GenericArguments.player("player".toText()),
                                        permissions.getOthers()
                                )
                        )
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val player = source.requirePlayerOrArg(args, "player")
        val account = economy.getOrCreateAccount(player.uniqueId).orNull() ?: throw CommandException("Unable to get EconomyAccount. Wrong user?".toText())

        val message = "Currency Report for ${player.name}:".toText().green().concat(Text.NEW_LINE).toBuilder()
        economy.currencies.forEach {
            if (account.hasBalance(it)) {
                message.append(it.format(account.getBalance(it))).append(Text.NEW_LINE)
            }
        }

        source.sendMessage(message.build())
        return CommandResult.success()
    }

    override fun getDescription(): String = "View your or someone else'es Nico & Maki Points"
}