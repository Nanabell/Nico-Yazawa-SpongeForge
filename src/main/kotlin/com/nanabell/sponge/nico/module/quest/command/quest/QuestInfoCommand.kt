package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

@Permissions(supportsOthers = true)
@RegisterCommand(["info"], QuestCommand::class)
class QuestInfoCommand : StandardCommand<QuestModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                NicoConstants.quest("quest".toText()),
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.user("user".toText()),
                                permissions.getOthers()
                        )
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<IQuest>("quest")
        val target = source.requireUserOrArg(args, "user")

        val messages = mutableListOf<Text>()
        messages.add("Quest: ".aqua().concat(quest.getText()))
        messages.add("Status: ".aqua().concat(questStatus(quest, target)))
        messages.add(taskText(quest, target))
        messages.add(rewardText(quest))
        messages.add(dependencyText(quest, target))

        if (source.hasPermission("${QuestEditCommand::class.getSubCommandPath()}.base")) {
            messages.add("[Edit]".yellow()
                    .action(TextActions.showText("Click here to edit the Quest...".gray()))
                    .action(TextActions.runCommand("/${QuestEditCommand::class.getCommandString()} ${quest.id}")))
        }

        pagination.builder().contents(messages)
                .title("Quest [".aqua().concat(quest.name.yellow()).concat("]".aqua()))
                .header("Information for: ".aqua().concat(target.name.yellow()))
                .sendTo(source)

        return CommandResult.success()
    }

    private fun questStatus(quest: IQuest, user: User): Text {
        if (quest.isComplete(user.uniqueId)) {
            return "Completed".green()
        }

        if (quest.isActive(user.uniqueId)) {
            return "Active".yellow()
        }

        return "Locked".red()
    }

    private fun taskText(quest: IQuest, user: User): Text {
        var message = "Tasks: ".aqua()
        if (quest.tasks.isEmpty()) {
            return message.concat("None".gray())
        }

        quest.tasks().forEach {
            message = message.concat(Text.NEW_LINE).concat(" - ".white()).concat(it.getText())

            val progress = it.getProgress(user.uniqueId)
            message = message.concat(NicoConstants.SPACE).concat(progress.getText().aqua())
        }

        return message
    }

    private fun rewardText(quest: IQuest): Text {
        var message = "Rewards: ".aqua()
        if (quest.rewards.isEmpty()) {
            return message.concat("None".gray())
        }

        quest.rewards().forEach {
            message = message.concat(Text.NEW_LINE).concat(" - ".white()).concat(it.getText())
        }

        return message
    }

    private fun dependencyText(quest: IQuest, user: User): Text {
        var message = "Requirements: ".aqua()
        if (quest.dependencies.isEmpty()) {
            return message.concat("None".gray())
        }

        quest.dependencies().forEach {
            message = message.concat(Text.NEW_LINE).concat(" - ".white()).concat(it.getText())

            if (it.isComplete(user.uniqueId)) {
                message = message.concat(" [Complete]".green())
            }
        }

        return message
    }

    override fun getDescription(): String = "View Information about a Quest"
}