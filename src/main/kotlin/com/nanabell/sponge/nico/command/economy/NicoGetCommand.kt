package com.nanabell.sponge.nico.command.economy

import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.economy.NicoCurrency
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text

class NicoGetCommand : CommandExecutor, SelfSpecCommand {

    private val serviceManager = Sponge.getServiceManager()

    override fun aliases(): Array<String> {
        return arrayOf("get")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .description(Text.of("View your current Nico Points"))
                .executor(this)
                .build()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {

        if (src !is Player) {
            throw CommandException(Text.of("This Command can only be used by players!"))
        }

        val service = serviceManager.provideUnchecked(EconomyService::class.java)
        val account = service.getOrCreateAccount(src.uniqueId).orElseThrow { CommandException(Text.of("Unable to create User Account for User$src")) }

        src.sendMessage(Text.of("You currently have ").concat(NicoCurrency.currency.format(account.getBalance(NicoCurrency.currency))))
        return CommandResult.success()
    }
}