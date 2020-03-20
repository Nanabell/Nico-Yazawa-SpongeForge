package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.RewardCommand
import com.nanabell.sponge.nico.module.quest.data.reward.MoneyReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*
import kotlin.reflect.KClass

@RegisterCommand(["new"], RewardCommand::class)
class RewardNewCommand : StandardCommand<QuestModule>() {

    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.choices("type".toText(), mapOf<String, KClass<out IReward>>(
                        "money" to MoneyReward::class
                )),
                GenericArguments.optional(
                        GenericArguments.remainingRawJoinedStrings("name".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val uniqueId = UUID.randomUUID()
        val name = args.getOne<String>("name").orNull() // TODO: Add names to Rewards

        val reward: IReward = when (args.requireOne<KClass<out IReward>>("type")) {
            MoneyReward::class -> MoneyReward(uniqueId, "Maki Points", 0)
            else -> throw IllegalArgumentException("Invalid Reward Type!")
        }

        rewardRegistry.set(reward)
        source.sendMessage(reward.getName()
                .concat(NicoConstants.SPACE)
                .concat(reward.getMessage()
                        .action(TextActions.showText("Click here to edit...".gray()
                                .concat(Text.NEW_LINE).concat(reward.id.toString().darkGray())))
                        .action(TextActions.runCommand("/reward edit ${reward.id}"))))

        return CommandResult.success()
    }

    override fun getDescription(): String = "Create a new Unattached Reward"
}