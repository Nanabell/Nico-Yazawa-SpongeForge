package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.UserRegistry
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

@RegisterCommand(["list"], QuestCommand::class)
class QuestListCommand : StandardCommand<QuestModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)
    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val userRegistry: UserRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.flags()
                        .flag("-all", "a")
                        .buildWith(GenericArguments.optional(
                                GenericArguments.user("user".toText())
                        ))
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val target = source.requireUserOrArg(args, "user")
        val quests = if (args.hasAny("all")) questRegistry.getAll() else userRegistry.get(target.uniqueId).getActiveQuests()

        val messages = mutableListOf<Text>()

        quests.forEach {
            messages.add(" - ".white().concat(questText(it, target)
                    .action(TextActions.showText("Click here for more infos...".gray()))
                    .action(TextActions.runCommand("/${QuestInfoCommand::class.getCommandString()} ${it.id}"))))
        }

        pagination.builder().contents(messages)
                .title("Quest list for ".green().concat(target.name.yellow()))
                .sendTo(source)

        return CommandResult.success()
    }

    private fun questText(quest: IQuest, user: User): Text {
        if (quest.isComplete(user.uniqueId)) {
            return quest.getName().concat(NicoConstants.SPACE).concat(quest.name.green()).aqua()
        }

        if (quest.isActive(user.uniqueId)) {
            return quest.getName().concat(NicoConstants.SPACE).concat(quest.name.yellow()).aqua()
        }

        return quest.getName().concat(NicoConstants.SPACE).concat(quest.name.red()).aqua()
    }

    override fun getDescription(): String = "List Active Quests. Add --all flag to list all quests"
}