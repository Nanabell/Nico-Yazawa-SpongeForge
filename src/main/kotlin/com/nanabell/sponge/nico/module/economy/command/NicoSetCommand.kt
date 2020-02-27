package com.nanabell.sponge.nico.module.economy.command

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.red
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
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal

@Permissions
@RegisterCommand(["set"], PointsCommand::class)
class NicoSetCommand : StandardCommand<EconomyModule>() {

    private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.playerOrSource("player".toText()),
                NicoConstants.currency("currency".toText()),
                GenericArguments.bigDecimal("amount".toText())
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val target = source.requirePlayerOrArg(args, "player")
        val currency = args.requireOne<Currency>("currency")
        val amount = args.requireOne<BigDecimal>("amount")

        val account = economy.getOrCreateAccount(target.uniqueId).orNull() ?: throw CommandException("Unable to get EconomyAccount. Wrong user?".toText())
        val result = account.setBalance(currency, amount, Cause.of(EventContext.of(mapOf(NicoConstants.COMMAND_SOURCE to source)), this))
        val message = if (result.result == ResultType.SUCCESS) {
            "${target.name}'s Balance has been set to ".toText().concat(currency.format(result.amount))
        } else {
            "Unable to set Balance to ".toText().red().concat(currency.format(result.amount))
        }

        source.sendMessage(message)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Set Nico or Maki Points for a User"

}