package com.nanabell.sponge.nico.module.activity.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.NoPlayerArgCommandException
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.formatDefault
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text

@Permissions
@RegisterCommand(["cooldown", "cd"], ActivityCommand::class)
class CooldownCommand : StandardCommand<ActivityModule>() {

    private val service: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.playerOrSource("player".toText()),
                                permissions.getOthers()
                        )
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        if (args.hasAny("player")) {
            val messages = mutableListOf<Text>()

            args.getAll<Player>("player").forEach {
                messages.add(getCooldownText(it))
            }

            source.sendMessage(Text.join(messages))
            return CommandResult.successCount(messages.size)
        }

        if (source !is Player) {
            throw NoPlayerArgCommandException()
        }

        val message = if (!service.isOnCooldown(source)) "You are not on cooldown".toText().green()
        else "You are on cooldown for ".toText().green().concat(service.getCooldown(source).formatDefault().toText().yellow())

        source.sendMessage(message)
        return CommandResult.success()
    }

    private fun getCooldownText(player: Player): Text {
        if (!service.isOnCooldown(player)) {
            return "${player.name} is not on cooldown!".toText().green()
        }

        val cooldown = service.getCooldown(player)
        return "Cooldown for ${player.name}: ".toText().green().concat(cooldown.formatDefault().toText().yellow())
    }

    override fun getDescription(): String = "How long until more nico points?"

}
