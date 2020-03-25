package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions
@RegisterCommand(["reward"], QuestEditCommand::class)
class QuestEditRewardCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val rewardRegistry: RewardRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.quest("quest".toText()),
                GenericArguments.optional(
                        NicoConstants.reward("reward".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<IQuest>("quest")
        val reward = args.getOne<IReward>("reward").orNull()

        if (reward == null) {
            source.sendMessage(printUnattachedTasks(quest))
            return CommandResult.success()
        }

        if (quest.rewards.contains(reward.id)) {
            quest.rewards.remove(reward.id)
        } else {
            quest.rewards.add(reward.id)
        }

        questRegistry.set(quest)
        return Sponge.getCommandManager().process(source, "quest edit ${quest.id}")
    }

    private fun printUnattachedTasks(quest: IQuest): Text {
        var message = "Unattached Rewards:".green().concat(Text.NEW_LINE)
        rewardRegistry.getAll().filter { !it.isAttached() }.forEach {
            message = message.concat(it.id.toString().yellow()
                    .action(TextActions.showText("Click here to Attach to Quest".gray()
                            .concat(Text.NEW_LINE)
                            .concat(it.getText().darkGray())))
                    .action(TextActions.runCommand("/quest edit reward ${quest.id} ${it.id}"))
            )
        }

        return message
    }

    override fun getDescription(): String = "Edit a Quests Rewards"
}