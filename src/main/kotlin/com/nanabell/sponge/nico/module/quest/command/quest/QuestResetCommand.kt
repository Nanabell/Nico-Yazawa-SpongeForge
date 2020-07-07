package com.nanabell.sponge.nico.module.quest.command.quest

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.darkRed
import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.command.QuestCommand
import com.nanabell.sponge.nico.module.quest.schedule.DailyQuestResetJob
import com.nanabell.sponge.nico.module.quest.schedule.WeeklyQuestResetJob
import org.quartz.JobKey
import org.quartz.SchedulerException
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.event.cause.Cause

@Permissions
@RegisterCommand(["reset"], QuestCommand::class)
class QuestResetCommand : StandardCommand<QuestModule>() {

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {

        try {
            NicoYazawa.getScheduler().triggerJob(JobKey.jobKey(DailyQuestResetJob::class.simpleName, QuestModule::class.simpleName))
            NicoYazawa.getScheduler().triggerJob(JobKey.jobKey(WeeklyQuestResetJob::class.simpleName, QuestModule::class.simpleName))
        } catch (e: SchedulerException) {
            logger.error("Failed to reset Daily & Weekly Quests!", e)
            throw CommandException("Failed to reset Daily & Weekly Quests!".darkRed())
        }

        source.sendMessage("Reset Daily & Weekly Quests!".green())
        return CommandResult.success()
    }

    override fun getDescription(): String = "Reset all Daily & Weekly Quests right now"
}