package com.nanabell.sponge.nico

import com.google.inject.Inject
import com.mongodb.MongoClient
import com.nanabell.sponge.nico.activity.ActivityTracker
import com.nanabell.sponge.nico.command.CommandRegistar
import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.economy.NicoEconomyService
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.LinkListener
import dev.morphia.Datastore
import dev.morphia.Morphia
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.service.economy.EconomyService
import java.nio.file.Path

@Plugin(id = "nico-yazawa", name = "Nico Yazawa", description = "Linking Minecraft & Discord Nico Style!", authors = ["Nanabell"], version = "0.1.1")
class NicoYazawa {

    @Inject
    private lateinit var logger: Logger

    @Inject
    @ConfigDir(sharedRoot = false)
    private lateinit var configDir: Path

    lateinit var configManager: Config<MainConfig>

    @Listener
    fun onPreInit(event: GamePreInitializationEvent) {
        instance = this
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        configManager = Config(MainConfig::class.java, "nicos-yazawa.conf", configDir)

        val morphia = Morphia()
        morphia.mapPackage("com.nanabell.sponge.nico.database.entity")

        val dataStore = morphia.createDatastore(MongoClient(), "dummy-nico")
        dataStore.ensureIndexes()

        val serviceManager = Sponge.getServiceManager()
        serviceManager.setProvider(this, Datastore::class.java, dataStore)
        serviceManager.setProvider(this, EconomyService::class.java, NicoEconomyService())
        serviceManager.setProvider(this, LinkService::class.java, LinkService())
        serviceManager.setProvider(this, DiscordService::class.java, DiscordService(this))
        serviceManager.setProvider(this, CommandRegistar::class.java, CommandRegistar(this))
        serviceManager.setProvider(this, ActivityTracker::class.java, ActivityTracker(this))
    }

    @Listener
    fun onGameAboutToStartServer(event: GameAboutToStartServerEvent) {
        Sponge.getEventManager().registerListeners(this, LinkListener())

        Sponge.getServiceManager().provide(ActivityTracker::class.java).orNull()!!.init()
    }

    @Listener
    fun onGameReload(event: GameReloadEvent) {
        configManager.reload()
    }

    companion object {
        lateinit var instance: NicoYazawa
            private set

        @JvmStatic
        fun getLogger(): Logger {
            return instance.logger
        }
    }
}