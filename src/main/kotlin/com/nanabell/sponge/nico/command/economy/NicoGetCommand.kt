package com.nanabell.sponge.nico.command.economy

import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.economy.NicoCurrency
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
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text

class NicoGetCommand : CommandExecutor, SelfSpecCommand {

    private val serviceManager = Sponge.getServiceManager()

    override fun aliases(): Array<String> {
        return arrayOf("get")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .permission("nico.command.points.get.execute")
                .description(Text.of("View your current Nico Points"))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder): List<PermissionDescription> {
        return listOf(builder.id("nico.command.points.get.self").register())
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) {
            throw CommandException(Text.of("This Command can only be used by players!"))
        }

        val service = serviceManager.provideUnchecked(EconomyService::class.java)
        val account = service.getOrCreateAccount(src.uniqueId).orNull()
        if (account == null) {
            src.sendMessage("You need to Link your Discord Account to be able to use Nico Points!".toText().red())
            return CommandResult.success()
        }

        src.sendMessage("You currently have ".toText().concat(NicoCurrency.currency.format(account.getBalance(NicoCurrency.currency))))
        return CommandResult.success()
    }
}