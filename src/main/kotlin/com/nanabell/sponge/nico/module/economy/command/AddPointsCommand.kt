package com.nanabell.sponge.nico.module.economy.command

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.red
import com.nanabell.sponge.nico.internal.extension.toText
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
@RegisterCommand(["add"], PointsCommand::class)
class AddPointsCommand : StandardCommand<EconomyModule>() {

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

        val result = account.deposit(currency, amount, cause)
        if (result.result == ResultType.SUCCESS) {

            source.sendMessage("Added ".toText().green()
                    .concat(currency.format(amount))
                    .concat(" to ${target.name}".toText().green()))

            if (!args.hasAny("silent"))
                target.player.ifPresent {
                    it.sendMessage("You have been awarded with ".toText().green()
                            .concat(currency.format(amount))
                            .concat(" from ${source.name}".toText().green()))
                }

            return CommandResult.success()
        } else {
            source.sendMessage("Failed to deposit ".toText()
                    .concat(currency.format(amount))
                    .concat(" to user ${target.name}, result: ${result.result}".toText().red()))

            return CommandResult.empty()
        }
    }

    override fun getDescription(): String = "Add Points to a User"

    override fun getExtendedDescription(): String? = "Add to a Users Currency Account" +
            "\n[--silent|-s] silent = Do not inform the user that points have been added"
}