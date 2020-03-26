package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
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
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.Currency

@Permissions
@RegisterCommand(["currency"], RewardEditCommand::class)
class RewardEditCurrencyCommand : StandardCommand<QuestModule>() {

    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.reward("reward".toText(), true),
                NicoConstants.currency("currency".toText())
        )
    }
    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val reward = args.requireOne<IReward>("reward")
        val currency = args.requireOne<Currency>("currency")

        when (reward) {
            is MoneyReward -> reward.currency = currency
            else -> throw IllegalArgumentException("Reward $reward does not have a Currency property!")
        }

        rewardRegistry.set(reward)
        return Sponge.getCommandManager().process(source, "reward edit ${reward.id}")
    }

    override fun getDescription(): String = "Edit a Rewards Currency"
}