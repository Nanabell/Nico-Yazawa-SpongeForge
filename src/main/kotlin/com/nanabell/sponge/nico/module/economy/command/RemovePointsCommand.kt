package com.nanabell.sponge.nico.module.economy.command

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.economy.EconomyModule
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal

@Permissions
@RegisterCommand(["remove"], PointsCommand::class)
class RemovePointsCommand : StandardCommand<EconomyModule>() {

    val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.currency("currency".toText()),
                GenericArguments.flags()
                        .flag("-silent", "s")
                        .buildWith(GenericArguments.onlyOne(GenericArguments.user("user".toText()))),
                GenericArguments.bigDecimal("amount".toText())
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val currency = args.requireOne<Currency>("currency")
        val target = args.requireOne<User>("user")
        val amount = args.requireOne<BigDecimal>("amount")

        val account = economy.getOrCreateAccount(target.uniqueId).orNull()
        if (account == null) {
            source.sendMessage("User ${target.name} does not have an Economy Account!".toText().red())
            return CommandResult.empty()
        }

        if (!account.hasBalance(currency)) {
            source.sendMessage("User ${target.name} does not have an Account for the Currency ${currency.name}".toText().red())
            return CommandResult.empty()
        }

        val result = account.withdraw(currency, amount, cause)
        if (result.result != ResultType.SUCCESS) {
            source.sendMessage("Removed ".toText().green()
                    .concat(currency.format(amount))
                    .concat(" from ${target.name}".toText().green()))

            if (!args.hasAny("silent"))
                target.player.ifPresent {
                    it.sendMessage("${source.name} has removed ".toText().red()
                            .concat(currency.format(amount))
                            .concat(" from your Account!".toText().red()))
                }

        } else {
            if (result.result == ResultType.ACCOUNT_NO_FUNDS) {
                source.sendMessage("Unable to withdraw $amount, User ${target.name} only has ${account.getBalance(currency)}".toText().red())
            } else {
                source.sendMessage("Unknown withdraw Failure: ${result.result}".toText().darkRed())
            }
        }

        return CommandResult.success()
    }

    override fun getDescription(): String = "Remove Currency fom a User Account"

    override fun getExtendedDescription(): String? = "Remove from a Users Currency Account" +
            "\n[--silent|-s] silent = Do not inform the user that points have been removed"
}