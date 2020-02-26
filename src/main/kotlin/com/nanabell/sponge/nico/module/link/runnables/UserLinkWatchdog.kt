package com.nanabell.sponge.nico.module.link.runnables

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.core.service.PlaytimeService
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.config.KickConfig
import com.nanabell.sponge.nico.module.link.service.LinkService
import org.spongepowered.api.Sponge
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-A-LinkWatchdog", isAsync = true)
class UserLinkWatchdog : AbstractRunnable<LinkModule>() {

    private val linkService = NicoYazawa.getServiceRegistry().provideUnchecked(LinkService::class)
    private val playtimeService = NicoYazawa.getServiceRegistry().provideUnchecked(PlaytimeService::class)

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

            val duration = Duration.between(Instant.now(), playtimeService.computeIfAbsent(player.uniqueId) { Instant.now() })
            if (duration.seconds > config.kickPlaytime) {
                player.kick(config.information.replace("{playtime}", format(duration)).toText())
                logger.info("${player.name} has been kicked after being on the server for ${format(duration)} and not having their Account linked to Discord")
            }
        }
        logger.debug("Finished UserLinkWatchdog")
    }

    private fun format(duration: Duration): String {
        return duration.toString()
                .substring(2)
                .replace("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase()
    }
}