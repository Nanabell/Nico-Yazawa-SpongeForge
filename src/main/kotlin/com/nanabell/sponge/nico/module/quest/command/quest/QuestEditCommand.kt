package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.command.reward.RewardEditCommand
import com.nanabell.sponge.nico.module.quest.command.task.TaskEditCommand
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
import java.util.*

@Permissions
@RegisterCommand(["edit"], QuestCommand::class)
class QuestEditCommand : StandardCommand<QuestModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)

    override fun getArguments(): Array<CommandElement> = arrayOf(NicoConstants.quest("quest".toText()))

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val quest = args.requireOne<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest>("quest")
        val messages = mutableListOf<Text>()

        messages.add("ID: ".green().concat(quest.id.toString().yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        messages.add("Type: ".green().concat(quest.type.yellow()
                .action(TextActions.showText("Cannot be edited!".darkGray()))))

        messages.add("Name: ".green().concat(quest.name.yellow()
                .action(TextActions.showText("Click to edit...".gray()))
                .action(TextActions.suggestCommand("/${QuestEditNameCommand::class.getCommandString()} ${quest.id} "))))

        messages.add("Description: ".green().concat((if (quest.description.isNullOrBlank()) "None".gray() else quest.description!!.yellow())
                .action(TextActions.showText("Click to edit...".gray()))
                .action(TextActions.suggestCommand("/${QuestEditDescriptionCommand::class.getCommandString()} ${quest.id} "))))

        messages.add("Tasks: ".green().concat(listTasks(quest.id, quest.tasks())))
        messages.add("Rewards: ".green().concat(listRewards(quest.id, quest.rewards())))
        messages.add("Dependencies: ".green().concat(listDependencies(quest.id, quest.dependencies())))

        messages.add("[Delete?]".red()
                .action(TextActions.showText("Click to delete the Quest...".gray()))
                .action(TextActions.runCommand("/${QuestDeleteCommand::class.getCommandString()} ${quest.id}")))

        pagination.builder().contents(messages)
                .title("Editing Quest ".green().concat(quest.name.yellow()))
                .sendTo(source)

        return CommandResult.success()
    }

    private fun listTasks(questId: UUID, tasks: List<com.nanabell.sponge.nico.module.quest.interfaces.task.ITask>): Text {
        var message = listHeader("task", questId)
        if (tasks.isEmpty()) return message

        tasks.forEach {
            message = message.concat(Text.NEW_LINE)
                    .concat(" - ".white())
                    .concat("[${it.id}]".yellow()
                            .action(TextActions.showText("Click to remove...".gray()
                                    .concat(Text.NEW_LINE).concat(it.getText())))
                            .action(TextActions.runCommand("/${QuestEditTasksCommand::class.getCommandString()} $questId ${it.id}")))
                    .concat(NicoConstants.SPACE)
                    .concat("[Edit]".gray()
                            .action(TextActions.showText("Click to edit...".gray()))
                            .action(TextActions.runCommand("/${TaskEditCommand::class.getCommandString()} ${it.id}")))
        }

        return message
    }

    private fun listRewards(questId: UUID, tasks: List<IReward>): Text {
        var message = listHeader("reward", questId)
        if (tasks.isEmpty()) return message

        tasks.forEach {
            message = message.concat(Text.NEW_LINE)
                    .concat(" - ".white())
                    .concat("[${it.id}]".yellow()
                            .action(TextActions.showText("Click to remove...".gray()
                                    .concat(Text.NEW_LINE).concat(it.getText())))
                            .action(TextActions.runCommand("/${QuestEditRewardCommand::class.getCommandString()} $questId ${it.id}")))
                    .concat(NicoConstants.SPACE)
                    .concat("[Edit]".gray()
                            .action(TextActions.showText("Click to edit...".gray()))
                            .action(TextActions.runCommand("/${RewardEditCommand::class.getCommandString()} ${it.id}")))
        }

        return message
    }

    private fun listDependencies(questId: UUID, quests: List<com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest>): Text {
        var message = listHeader("quest", questId)
        if (quests.isEmpty()) return message

        quests.forEach {
            message = message.concat(Text.NEW_LINE)
                    .concat(" - ".white())
                    .concat("[${it.id}]".yellow()
                            .action(TextActions.showText("Click to remove...".gray()
                                    .concat(Text.NEW_LINE).concat(it.getText())))
                            .action(TextActions.runCommand("/${QuestEditDependenciesCommand::class.getCommandString()} $questId ${it.id}")))
                    .concat(NicoConstants.SPACE)
                    .concat("[Edit]".gray()
                            .action(TextActions.showText("Click to edit...".gray()))
                            .action(TextActions.runCommand("/${QuestEditCommand::class.getCommandString()} ${it.id}")))
        }

        return message
    }

    private fun listHeader(rawType: String, questId: UUID): Text {
        val type = if (rawType == "quest") "dependency" else rawType

        return " [Add]".aqua()
                .action(TextActions.showText("Click to add existing,,,".gray()))
                .action(TextActions.runCommand("/quest edit $type $questId "))
                .concat(" [new]".blue()
                        .action(TextActions.showText("Click to create new...".gray()))
                        .action(TextActions.suggestCommand("/$rawType new ")))
    }

    override fun getDescription(): String = "Edit a Quest"
}