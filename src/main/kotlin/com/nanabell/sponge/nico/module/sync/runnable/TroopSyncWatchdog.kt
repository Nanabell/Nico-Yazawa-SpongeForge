package com.nanabell.sponge.nico.module.sync.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.sync.SyncModule
import com.nanabell.sponge.nico.module.sync.config.SyncConfig
import com.nanabell.sponge.nico.module.sync.misc.TroopSource
import com.nanabell.sponge.nico.module.sync.service.TroopSyncService
import org.spongepowered.api.Sponge
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-A-SyncService", delay = 5, delayUnit = TimeUnit.MINUTES, interval = 5, intervalUnit = TimeUnit.MINUTES, isAsync = true)
class TroopSyncWatchdog : AbstractRunnable<SyncModule>() {

    private val service: TroopSyncService = NicoYazawa.getServiceRegistry().provideUnchecked(TroopSyncService::class)

    private lateinit var config: SyncConfig

    override fun onReady() {
        this.config = module.getConfigOrDefault()
    }

    override fun run() {
        if (!config.discordSync && !config.minecraftSync)
            return

        logger.debug("Starting Sync run")
        for (player in Sponge.getServer().onlinePlayers) {

            if (config.discordSync)
                service.sync(player, TroopSource.DISCORD)

            if (config.minecraftSync)
                service.sync(player, TroopSource.MINECRAFT)

        }
        logger.debug("Finished Sync run")
    }

}