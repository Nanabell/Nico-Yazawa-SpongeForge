package com.nanabell.sponge.nico.module.info.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.StandardCommand
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.core.command.NicoCommand
import com.nanabell.sponge.nico.module.economy.data.currency.NicoCurrency
import com.nanabell.sponge.nico.module.info.InfoModule
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text

@Permissions(supportsOthers = true)
@RegisterCommand(["info"], NicoCommand::class)
class InfoCommand : StandardCommand<InfoModule>() {

    private val pagination = Sponge.getServiceManager().provideUnchecked(PaginationService::class.java)
    private val playtime: PlaytimeService? = NicoYazawa.getServiceRegistry().provide()
    private val activity: ActivityService? = NicoYazawa.getServiceRegistry().provide()
    private val economy: EconomyService? = NicoYazawa.getServiceRegistry().provide()
    private val link: LinkService? = NicoYazawa.getServiceRegistry().provide()

    override fun getArguments(): Array<CommandElement> {
        return arrayOf(GenericArguments.optional(
                GenericArguments.onlyOne(
                        GenericArguments.requiringPermission(
                                GenericArguments.player("player".toText()),
                                permissions.getOthers()
                        )
                )
        ))
    }

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val target = source.requirePlayerOrArg(args, "player")

        // Player Name & Id
        val messages = mutableListOf<Text>()

        // Generic Info
        messages.add("General".toText().yellow())
        messages.add("ID: ".toText().aqua().concat(target.uniqueId.toString().toText().yellow()))
        messages.add("First Played: ".toText().aqua().concat(target.joinData.firstPlayed().get().formatDefault().toText().yellow()))
        messages.add("Last Played: ".toText().aqua().concat(target.joinData.lastPlayed().get().formatDefault().toText().yellow()))
        messages.add("Gamemode: ".toText().aqua().concat(target.gameMode().get().name.toText().yellow()))

        if (playtime != null) {
            messages.add(Text.EMPTY)
            messages.add("Playtime".toText().yellow())

            messages.add("Total Play Time: ".toText().aqua().concat(playtime.getPlayTime(target).formatDefault().toText().yellow()))
            messages.add("Total Afk Time: ".toText().aqua().concat(playtime.getAfkTime(target).formatDefault().toText().yellow()))
            messages.add("Total Active Time: ".toText().aqua().concat(playtime.getActiveTime(target).formatDefault().toText().yellow()))

            messages.add("Session Play Time: ".toText().aqua().concat(playtime.getSessionPlayTime(target).formatDefault().toText().yellow()))
            messages.add("Session Afk Time: ".toText().aqua().concat(playtime.getSessionAfkTime(target).formatDefault().toText().yellow()))
            messages.add("Session Active Time: ".toText().aqua().concat(playtime.getSessionActiveTime(target).formatDefault().toText().yellow()))
        }

        if (activity != null) {
            messages.add(Text.EMPTY)
            messages.add("Activity".toText().yellow())
            messages.add("Rewards Today: ".toText().aqua().concat(NicoCurrency.instance.format(activity.getPayoutAmount(target)).yellow()))
            messages.add("Cooldown: ".toText().aqua().concat(activity.getCooldown(target).formatDefault().toText().yellow()))
        }

        if (economy != null) {
            messages.add(Text.EMPTY)
            messages.add("Economy".toText().yellow())

            val account = economy.getOrCreateAccount(target.uniqueId).orNull()
            if (account != null) {
                economy.currencies.forEach {
                    if (account.hasBalance(it)) {
                        messages.add("${it.name}: ".toText().aqua().concat(it.format(account.getBalance(it)).yellow()))
                    } else {
                        messages.add("${it.name}: ".toText().concat("No Balance".toText().gray()))
                    }
                }
            } else {
                messages.add("No Account!".toText().gray())
            }
        }

        if (link != null) {
            messages.add(Text.EMPTY)
            messages.add("Link".toText().yellow())

            val linked = link.getLink(target)
            if (linked != null) {
                messages.add("Link: ".toText().aqua().concat(target.name.toText().yellow())
                        .concat(" = ".toText().aqua())
                        .concat((linked.fetchDiscordUser()?.asTag?.toText()?.yellow()
                                ?: "[Failed to fetch User!]".toText().red()))
                        .concat(" (".toText().white().concat(linked.discordId.toString().toText().gold()).concat(") ".toText().white())))

            } else {
                messages.add("Link: ".toText().aqua().concat("Account not Linked!".toText().gray()))
            }
        }

        pagination.builder()
                .title("Information for ".toText().green().concat(target.name.toText().yellow()))
                .contents(messages)
                .sendTo(source)
        return CommandResult.success()
    }

    override fun getDescription(): String = "Return Detailed Information about yourself or a Target User"

    override fun getExtendedDescription(): String? {
        return "Returns a few Basic Information and everything Nico tracks about the Target\n" +
                "You will need permission to view each of the sub-sections" // TODO: Actually make Permission required for the sections!
    }
}
