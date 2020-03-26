package com.nanabell.sponge.nico.module.quest.command.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.RewardCommand
import com.nanabell.sponge.nico.module.quest.data.reward.InvalidReward
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions
@RegisterCommand(["delete"], RewardCommand::class)
class RewardDeleteCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-force", "f")
                        .buildWith(NicoConstants.reward("reward".toText(), true))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val reward = args.requireOne<IReward>("reward")
        if (reward is InvalidReward) {
            source.sendMessage("There is no Reward with the id ${reward.id}".red())
            return CommandResult.empty()
        }

        if (!args.hasAny("force")) {
            source.sendMessage(confirmMessage(reward))
            return CommandResult.success()
        }

        rewardRegistry.remove(reward)
        questRegistry.getAll().forEach { quest ->
            if (quest.rewards.contains(reward.id)) {
                quest.rewards.remove(reward.id)
                questRegistry.set(quest)
            }
        }

        source.sendMessage("[".green()
                .concat(reward.getText())
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .action(TextActions.showText(reward.id.toString().darkGray()))
                .concat(" has been permanently deleted!".green()))

        return CommandResult.success()
    }

    private fun confirmMessage(reward: IReward): Text {
        return "Are you sure you want to delete the reward: ".green()
                .concat(Text.NEW_LINE)
                .concat("[".green())
                .concat(reward.getText().action(TextActions.showText(reward.id.toString().darkGray())))
                .concat("]".green())
                .concat(Text.NEW_LINE)
                .concat(Text.NEW_LINE)
                .concat("This action cannot be undone!".red())
                .concat(Text.NEW_LINE)
                .concat("The reward will be removed from any attached Quests!".red())
                .concat(Text.NEW_LINE)
                .concat("[Confirm]".darkRed()
                        .action(TextActions.showText("Click here to delete the reward...".gray()))
                        .action(TextActions.runCommand("/${this::class.getCommandString()} ${reward.id} -f")))
    }

    override fun getDescription(): String = "Delete a Reward from the System"
}