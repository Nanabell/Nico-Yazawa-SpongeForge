package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.RewardCommand
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@RegisterCommand(["edit"], RewardCommand::class)
class RewardEditCommand : StandardCommand<QuestModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)

    override fun getArguments(): Array<CommandElement> = arrayOf(NicoConstants.reward("reward".toText()))

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val reward = args.requireOne<IReward>("reward")
        val messages = mutableListOf<Text>()

        messages.add("ID: ".green().concat(reward.id.toString().yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        messages.add("Type: ".green().concat(reward.type.yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        pagination.builder().contents(messages.plus(reward.printSettings()))
                .title("Editing Reward ".green().concat(reward.type.yellow()))
                .sendTo(source)

        return CommandResult.success()
    }

    override fun getDescription(): String = "View the Settings of a Reward"
}