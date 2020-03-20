package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.data.quest.DailyQuest
import com.nanabell.sponge.nico.module.quest.data.quest.SimpleQuest
import com.nanabell.sponge.nico.module.quest.data.quest.WeeklyQuest
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*

@RegisterCommand(["new"], QuestCommand::class)
class QuestNewCommand : StandardCommand<QuestModule>() {

    private val questRegistry: QuestRegistry = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(
                GenericArguments.choices("type".toText(), mapOf(
                        "simple" to "Simple",
                        "daily" to "Daily",
                        "weekly" to "Weekly"
                )),
                GenericArguments.optional(
                        GenericArguments.remainingRawJoinedStrings("name".toText())
                )
        )
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val uniqueId = UUID.randomUUID()
        val name = args.getOne<String>("name").orNull()

        val quest: IQuest = when (args.requireOne<String>("type")) {
            "Simple" -> SimpleQuest(uniqueId, "New Simple Quest", null)
            "Daily" -> DailyQuest(uniqueId, "New Daily Quest", null)
            "Weekly" -> WeeklyQuest(uniqueId, "New Weekly Quest", null)
            else -> throw IllegalArgumentException("Invalid Argument Type!")
        }

        if (name != null) {
            quest.name = name
        }

        questRegistry.set(quest)
        source.sendMessage(quest.getName()
                .concat(NicoConstants.SPACE)
                .concat(quest.getMessage()
                        .action(TextActions.showText("Click here to edit...".gray()
                                .concat(Text.NEW_LINE)
                                .concat(quest.id.toString().darkGray())))
                        .action(TextActions.runCommand("/quest edit ${quest.id}"))))

        return CommandResult.success()
    }

    override fun getDescription(): String = "Add a new Quest"
}