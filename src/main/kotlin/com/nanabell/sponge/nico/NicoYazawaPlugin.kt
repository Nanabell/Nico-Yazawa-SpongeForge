package com.nanabell.sponge.nico

import com.google.inject.Inject
import com.nanabell.quickstart.container.DiscoveryModuleContainer
import com.nanabell.sponge.nico.internal.InternalServiceRegistry
import com.nanabell.sponge.nico.internal.PermissionRegistry
import com.nanabell.sponge.nico.internal.command.RegisterCommandRequestEvent
import com.nanabell.sponge.nico.internal.interfaces.Reloadable
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.plugin.Plugin
import java.nio.file.Path


@Plugin(id = "nico-yazawa", name = "Nico Yazawa", description = "Linking Minecraft & Discord Nico Style!", authors = ["Nanabell"], version = "0.1.1")
class NicoYazawaPlugin @Inject constructor(@ConfigDir(sharedRoot = false) private val configDir: Path, private val _logger: Logger) : NicoYazawa() {

    private val logger = getLogger("Main")

    private val permissionRegistry: PermissionRegistry = PermissionRegistry()
    private val serviceRegistry: InternalServiceRegistry = InternalServiceRegistry()

    private val reloadables: MutableList<Reloadable> = ArrayList()

    private lateinit var moduleContainer: DiscoveryModuleContainer

    init {
        setPlugin(this)
        logger.info("Hello World!")
    }

    @Listener
    fun preInit(event: GamePreInitializationEvent) {
        try {
            val moduleConfig = HoconConfigurationLoader.builder()
                    .setPath(configDir.resolve("nico.conf"))
                    .build()

            moduleContainer = DiscoveryModuleContainer.builder()
                    .setConfigurationLoader(moduleConfig)
                    .setModuleConfigKey("-modules")
                    .setPackageToScan(javaClass.`package`.name + ".module")
                    .setLogger(getLogger("Module Discovery"))
                    .build(true)
        } catch (e: Exception) {
            logger.error("Plugin PreInitialization failed!", e)
            disable()
        }
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        try {
            moduleContainer.refreshSystemConfig()
            moduleContainer.loadModules(true)
        } catch (e: Exception) {
            logger.error("Plugin Initialization failed!", e)
            disable()
        }

        permissionRegistry.registerPermissions()
        moduleContainer.reloadSystemConfig()
    }

    @Listener
    fun onGameStart(event: GameStartedServerEvent) {
        moduleContainer.refreshSystemConfig()
        reloadAll()
    }

    @Listener
    fun onGameReload(event: GameReloadEvent) {
        moduleContainer.refreshSystemConfig()
        reloadAll()
    }

    @Listener(order = Order.POST)
    fun onRegisterCommandRequest(event: RegisterCommandRequestEvent) {
        if (!event.isRegistered) {
            logger.error("Command ${event.command::class.simpleName} has requested registration to class ${event.clazz.simpleName} but was never registered!")
        }
    }

    private fun reloadAll() {
        reloadables.forEach { it.onReload() }
    }

    private fun disable() {
        Sponge.getEventManager().unregisterPluginListeners(this)
        Sponge.getCommandManager().getOwnedBy(this).forEach { Sponge.getCommandManager().removeMapping(it) }
        Sponge.getScheduler().getScheduledTasks(this).forEach { it.cancel() }

        logger.error("Plugin Initialization has failed. Unable to continue")
        Sponge.getServer().shutdown()
    }

    override fun getLogger(vararg topics: String): TopicLogger {
        if (topics.isEmpty())
            return logger

        return TopicLogger(_logger, *topics)
    }

    override fun registerReloadable(reloadable: Reloadable) {
        reloadables.add(reloadable)
    }

    override fun getPermissionRegistry(): PermissionRegistry {
        return permissionRegistry
    }

    override fun getModuleContainer(): DiscoveryModuleContainer {
        return moduleContainer
    }

    override fun getServiceRegistry(): InternalServiceRegistry {
        return serviceRegistry
    }
}