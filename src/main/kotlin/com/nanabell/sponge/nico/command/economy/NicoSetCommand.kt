package com.nanabell.sponge.nico.command.economy

import com.nanabell.sponge.nico.command.SelfSpecCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text

class NicoSetCommand : CommandExecutor, SelfSpecCommand {

    private val serviceManager = Sponge.getServiceManager()

    override fun aliases(): Array<String> {
        return arrayOf("set")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("Set your Balance to a specified amount"))
                .permission("nico.command.points.set.self")
                .arguments(GenericArguments.bigDecimal(Text.of("amount")))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.points.set.self").register()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) {
            throw CommandException(Text.of("This Command can only be used by players!"))
        }
        if (!args.getOne<Any>("amount").isPresent) {
            throw CommandException(Text.of("You must specify the amount that the account will be set to!"))
        }

        val service = serviceManager.provideUnchecked(EconomyService::class.java)
        val account = service.getOrCreateAccount(src.uniqueId).orElseThrow { CommandException(Text.of("Unable to created Economy Account!")) }

        val result = account.setBalance(service.defaultCurrency, args.requireOne("amount"), Cause.of(EventContext.empty(), this))
        if (result.result == ResultType.SUCCESS) {
            src.sendMessage(Text.of("Your Balance has been set to ")
                    .concat(service.defaultCurrency.format(result.amount)))
        } else {
            src.sendMessage(Text.of("Unable to set Balance to ")
                    .concat(service.defaultCurrency.format(result.amount))
                    .concat(Text.NEW_LINE)
                    .concat(Text.of("Reason: "))
                    .concat(Text.of(result.type.name)))
        }
        return CommandResult.success()
    }

}