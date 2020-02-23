package com.nanabell.sponge.nico

import com.google.inject.Inject
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.nanabell.sponge.nico.activity.ActivityService
import com.nanabell.sponge.nico.command.CommandRegistar
import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig
import com.nanabell.sponge.nico.economy.NicoEconomyService
import com.nanabell.sponge.nico.link.LinkService
import com.nanabell.sponge.nico.link.UserLinkWatchdog
import com.nanabell.sponge.nico.link.discord.DiscordService
import com.nanabell.sponge.nico.link.sync.TroopSyncService
import dev.morphia.Datastore
import dev.morphia.Morphia
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePostInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.service.economy.EconomyService
import java.nio.file.Path


@Plugin(id = "nico-yazawa", name = "Nico Yazawa", description = "Linking Minecraft & Discord Nico Style!", authors = ["Nanabell"], version = "0.1.1")
class NicoYazawaPlugin @Inject constructor(@ConfigDir(sharedRoot = false) private val configDir: Path, private val _logger: Logger) : NicoYazawa() {

    private val logger = getLogger("Main")
    private val config = Config(MainConfig::class.java, "nicos-yazawa.conf", configDir)

    init {
        setPlugin(this)
        logger.info("Hello World!")
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        val morphia = Morphia()
        val dataStore = morphia.createDatastore(MongoClient(MongoClientURI(config.get().databaseUrl)), "dummy-nico")
        dataStore.ensureIndexes()

        val serviceManager = Sponge.getServiceManager()
        serviceManager.setProvider(this, Datastore::class.java, dataStore)
        serviceManager.setProvider(this, EconomyService::class.java, NicoEconomyService())
        serviceManager.setProvider(this, LinkService::class.java, LinkService())
        serviceManager.setProvider(this, DiscordService::class.java, DiscordService())
        serviceManager.setProvider(this, CommandRegistar::class.java, CommandRegistar())
        serviceManager.setProvider(this, ActivityService::class.java, ActivityService())
        serviceManager.setProvider(this, TroopSyncService::class.java, TroopSyncService())
    }

    @Listener
    fun onPostInit(event: GamePostInitializationEvent) {
        val serviceManager = Sponge.getServiceManager()

        serviceManager.setProvider(this, UserLinkWatchdog::class.java, UserLinkWatchdog())
    }

    @Listener
    fun onGameAboutToStartServer(event: GameAboutToStartServerEvent) {
        val serviceManager = Sponge.getServiceManager()

        serviceManager.provideUnchecked(LinkService::class.java).init()
        serviceManager.provideUnchecked(DiscordService::class.java).init()
        serviceManager.provideUnchecked(ActivityService::class.java).init()
        serviceManager.provideUnchecked(TroopSyncService::class.java).init()
        serviceManager.provideUnchecked(UserLinkWatchdog::class.java).init()
    }

    @Listener
    fun onGameReload(event: GameReloadEvent) {
        // Reload Config
        config.reload().also { _logger.info("Reloaded Config") }

        // Reload Commands
        val commandRegistar = Sponge.getServiceManager().provideUnchecked(CommandRegistar::class.java)
        val commandManager = Sponge.getCommandManager()

        commandManager.getOwnedBy(this).forEach {
            commandManager.removeMapping(it)
        }
        commandRegistar.loadCommands().also { _logger.info("Reloaded Commands") }
    }

    override fun getLogger(vararg topics: String): TopicLogger {
        if (topics.isEmpty())
            return logger

        return TopicLogger(_logger, *topics)
    }

    override fun getConfig(): Config<MainConfig> {
        return config
    }
}