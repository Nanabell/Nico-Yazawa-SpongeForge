package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.reward.MoneyReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause

@RegisterCommand(["amount"], RewardEditCommand::class)
class RewardEditAmountCommand : StandardCommand<QuestModule>() {

    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.reward("reward".toText()),
                GenericArguments.integer("amount".toText())
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val reward = args.requireOne<IReward>("reward")
        val amount = args.requireOne<Int>("amount")

        when (reward) {
            is MoneyReward -> reward.amount = amount
            else -> throw IllegalArgumentException("Reward $reward does not have a Amount property!")
        }

        rewardRegistry.set(reward)
        return Sponge.getCommandManager().process(source, "reward edit ${reward.id}")
    }

    override fun getDescription(): String = "Edit a Rewards Amount"
}