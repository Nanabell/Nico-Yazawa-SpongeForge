package com.nanabell.sponge.nico.link

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.Sponge
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class UserLinkWatchdog(private val plugin: NicoYazawa) {

    private val linkService = Sponge.getServiceManager().provideUnchecked(LinkService::class.java)

    fun init() {
        Sponge.getScheduler().createTaskBuilder()
                .name("NicoYazawa-A-LinkWatchdog")
                .async()
                .delay(10, TimeUnit.MINUTES)
                .interval(5, TimeUnit.MINUTES) // TODO: Remember to make configurable
                .execute(runWatchdog())
                .submit(plugin)
    }


    fun runWatchdog(): Runnable {
        return Runnable {
            for (player in Sponge.getServer().onlinePlayers) {
                if (linkService.isLinked(player)) { // TODO: Possibly in the future retrieve all links at once
                    return@Runnable
                }

                val onServerSince = Duration.between(Instant.now(), player.joinData.lastPlayed().get())
                if (onServerSince > Duration.of(5, ChronoUnit.MINUTES)) {
                    player.kick("You need to Link your Minecraft Account to your Discord Account. Please read the Instructions at <INSERT-CHANNEL-NAME-HERE> and rejoin.".toText()) // TODO: Inject Channel name via config here
                }
            }
        }
    }
}