package com.nanabell.sponge.nico.command.economy

import com.nanabell.sponge.nico.command.Args
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.command.requirePlayerOrArg
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.internal.extension.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.text.Text

class NicoGetCommand : CommandExecutor, SelfSpecCommand {

    private val economy = Sponge.getServiceManager().provideUnchecked(EconomyService::class.java)

    override fun aliases(): Array<String> {
        return arrayOf("get")
    }

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .permission("nico.command.get.base")
                .description(Text.of("View your current Nico Points"))
                .arguments(Args.optional(Args.requiringPermission(Args.playerOrSource("player".toText()), "nico.command.get")))
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.get.base").register()
        builder.id("nico.command.get").register()
    }

    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = src.requirePlayerOrArg(args, "player")
        val account = economy.getOrCreateAccount(player.uniqueId).orNull()
                ?: throw CommandException("Unable to get EconomyAccount. Wrong user?".toText())

        val message = "Your Currency Report:".toText().concat(Text.NEW_LINE).toBuilder()
        economy.currencies.forEach {
            if (account.hasBalance(it)) {
                message.append(it.format(account.getBalance(it))).append(Text.NEW_LINE)
            }
        }

        src.sendMessage(message.build())
        return CommandResult.success()
    }
}