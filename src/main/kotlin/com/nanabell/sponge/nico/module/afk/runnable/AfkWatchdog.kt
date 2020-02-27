package com.nanabell.sponge.nico.module.afk.runnable

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.afk.AfkModule
import com.nanabell.sponge.nico.module.afk.config.AfkConfig
import com.nanabell.sponge.nico.module.afk.service.AfkService
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.time.Duration
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-S-AfkWatchdog", 5, TimeUnit.SECONDS, 1, TimeUnit.MINUTES)
class AfkWatchdog : AbstractRunnable<AfkModule>() {

    private val service: AfkService = NicoYazawa.getServiceRegistry().provideUnchecked()

    lateinit var config: AfkConfig

    override fun onReady() {
        this.config = module.getConfigOrDefault()
    }

    override fun run() {
        if (config.afkTimeout <= Duration.ZERO)
            return

        for (player in Sponge.getServer().onlinePlayers) {

            if (!service.isAfk(player)) {
                val inactivity = service.getInactiveDuration(player)

                if (inactivity > config.afkTimeout) {

                    if (service.isImmune(player))
                        return


                    service.startAfk(player, Cause.of(EventContext.of(mapOf(NicoConstants.INACTIVE to inactivity)), this))
                }
            }
        }
    }

    override fun onReload() {
        this.config = module.getConfigOrDefault()
    }

}