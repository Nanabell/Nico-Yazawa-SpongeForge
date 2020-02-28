package com.nanabell.sponge.nico.module.sync.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.extension.toText
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.activity.service.PlaytimeService
import com.nanabell.sponge.nico.module.link.service.LinkService
import com.nanabell.sponge.nico.module.sync.SyncModule
import com.nanabell.sponge.nico.module.sync.config.KickConfig
import org.spongepowered.api.Sponge
import java.time.Duration
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-A-LinkWatchdog", isAsync = true)
class UserLinkWatchdog : AbstractRunnable<SyncModule>() {

    private val linkService: LinkService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val playtimeService: PlaytimeService = NicoYazawa.getServiceRegistry().provideUnchecked()

    private lateinit var config: KickConfig

    override fun onReady() {
        this.config = module.getConfigOrDefault().kickConfig
    }

    override fun overrideDelay(): Pair<Long, TimeUnit>? {
        return (config.kickInterval * 2) to TimeUnit.SECONDS
    }

    override fun overrideInterval(): Pair<Long, TimeUnit>? {
        return config.kickInterval to TimeUnit.SECONDS
    }

    override fun run() {
        if (!config.enabled) return

        for (player in Sponge.getServer().onlinePlayers) {
            if (linkService.isLinked(player)) { // TODO: Possibly in the future retrieve all links at once
                continue
            }

            val duration = playtimeService.getSessionPlaytimeRaw(player)
            if (duration.seconds >= config.kickPlaytime) {
                player.kick(config.information.replace("{playtime}", format(duration)).toText())
                logger.info("${player.name} has been kicked after being on the server for ${format(duration)} and not having their Account linked to Discord")
            }
        }
        logger.debug("Finished UserLinkWatchdog")
    }

    override fun onReload() {
        this.config = module.getConfigOrDefault().kickConfig
    }

    private fun format(duration: Duration): String {
        return duration.toString()
                .substring(2)
                .replace("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase()
    }
}