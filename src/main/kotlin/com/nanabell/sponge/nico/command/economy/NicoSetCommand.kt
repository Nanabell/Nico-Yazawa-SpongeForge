package com.nanabell.sponge.nico.command.economy

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.red
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text
import java.math.BigDecimal

class NicoSetCommand : CommandExecutor, SelfSpecCommand {

    private val economy = Sponge.getServiceManager().provideUnchecked(EconomyService::class.java)

    override fun aliases(): Array<String> {
        return arrayOf("set")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Set your Balance to a specified amount"))
                .permission("nico.command.set.base")
                .arguments(Args.seq(
                        Args.playerOrSource("player".toText()),
                        NicoConstants.currency("currency".toText()),
                        Args.bigDecimal("amount".toText())
                ))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.set.base").register()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = src.requirePlayerOrArg(args, "player")
        val amount = args.requireOne<BigDecimal>("amount")
        val currency = args.requireOne<Currency>("currency")

        val account = economy.getOrCreateAccount(target.uniqueId).orNull()
                ?: throw CommandException("Unable to get EconomyAccount. Wrong user?".toText())


        val result = account.setBalance(currency, amount, Cause.of(EventContext.of(mapOf(NicoConstants.COMMAND_SOURCE to src)), this))
        val message = if (result.result == ResultType.SUCCESS) {
            "Your Balance has been set to ".toText().concat(currency.format(result.amount))
        } else {
            "Unable to set Balance to ".toText().red()
                    .concat(currency.format(result.amount))
                    .concat(Text.NEW_LINE)
                    .concat("Reason: ".toText().red())
                    .concat(Text.of(result.type.name))
        }

        src.sendMessage(message)
        return CommandResult.success()
    }

}