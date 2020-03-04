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
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text

@Permissions
@RegisterCommand(["points"])
class PointsCommand : StandardCommand<EconomyModule>() {

    private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-all", "a")
                        .valueFlag(
                                GenericArguments.onlyOne(NicoConstants.currency("currency".toText())),
                                "-currency", "c")
                        .buildWith(GenericArguments.optional(
                                GenericArguments.onlyOne(
                                        GenericArguments.requiringPermission(
                                                GenericArguments.user("user".toText()),
                                                permissions.getOthers()
                                        )
                                )
                        ))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val target = source.requireUserOrArg(args, "user")
        val currency = args.getOne<Currency>("currency").orNull() ?: economy.defaultCurrency

        val account = economy.getOrCreateAccount(target.uniqueId).orNull()
        if (account == null) {
            if (target == source) {
                source.sendMessage("You do not have an Economy Account!".toText().red())
            } else {
                source.sendMessage("User ${target.name} does not have an Economy Account!".toText().red())
            }

            return CommandResult.empty()
        }

        if (args.hasAny("all")) {
            val message = (if (target == source) "Your Currency Report:" else "Currency Report for ${target.name}:").toText()
            message.concat(Text.NEW_LINE)

            economy.currencies.forEach {
                if (account.hasBalance(it)) {
                    message.concat(it.format(account.getBalance(it))).concat(Text.NEW_LINE)
                }
            }

            source.sendMessage(message)
            return CommandResult.success()
        }

        if (!account.hasBalance(currency)) {
            if (target == source) {
                source.sendMessage("You do not have an Account for ${currency.name}!".toText().red())
            } else {
                source.sendMessage("User ${target.name} does not have an Account for ${currency.name}".toText().red())
            }

            return CommandResult.empty()
        }

        val message = (if (target == source) "You currently have " else "${target.name} currently has ").toText().green()

        source.sendMessage(message.concat(currency.format(account.getBalance(currency))))
        return CommandResult.success()
    }

    override fun getDescription(): String = "View your or someone else'es Nico & Maki Points"


}