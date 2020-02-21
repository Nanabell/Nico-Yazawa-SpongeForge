package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.util.*
import java.util.concurrent.TimeUnit

class UserLinkWatchdog(private val plugin: NicoYazawa) {

    private val logger = NicoYazawa.getLogger()
    private val config = NicoYazawa.getConfig()
    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    private val joinTimes: MutableMap<UUID, Long> = mutableMapOf()

    fun init() {
        Sponge.getEventManager().registerListeners(plugin, this)

        if (config.get().discordLinkConfig.kickUnlinked)
            Sponge.getScheduler().createTaskBuilder()
                    .name("NicoYazawa-A-LinkWatchdog")
                    .async()
                    .delay(config.get().discordLinkConfig.kickInterval * 2, TimeUnit.MINUTES)
                    .interval(config.get().discordLinkConfig.kickInterval, TimeUnit.MINUTES)
                    .execute(runWatchdog())
                    .submit(plugin)
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        joinTimes[event.targetEntity.uniqueId] = System.currentTimeMillis()
    }


    private fun runWatchdog(): Runnable {
        return Runnable {
            logger.debug("Starting UserLinkWatchdog...")
            for (player in Sponge.getServer().onlinePlayers) {
                if (linkService.isLinked(player)) { // TODO: Possibly in the future retrieve all links at once
                    continue
                }

                val seconds = (System.currentTimeMillis() - joinTimes[player.uniqueId]!!) / 1000
                if (seconds > config.get().discordLinkConfig.kickPlaytime * 60) {
                    player.kick("You need to Link your Minecraft Account to your Discord Account. Please read the Instructions at ${config.get().discordLinkConfig.kickReferChannel} and rejoin.".toText())
                    logger.info("${player.name} has been kicked after being on the server for $seconds seconds and not having their Account linked to Discord")
                }
            }
            logger.info("Finished UserLinkWatchdog")
        }
    }
}