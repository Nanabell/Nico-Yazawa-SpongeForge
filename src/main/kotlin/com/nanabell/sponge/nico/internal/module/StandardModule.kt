package com.nanabell.sponge.nico.internal.module

import com.nanabell.quickstart.AbstractModule
import com.nanabell.quickstart.RegisterModule
import com.nanabell.quickstart.config.ModuleConfig
import com.nanabell.quickstart.util.isInterface
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.command.CommandBuilder
import com.nanabell.sponge.nico.internal.command.RegisterCommandRequestEvent
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.internal.listener.ListenerBuilder
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.internal.runnable.RunnableBuilder
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.internal.service.ServiceBuilder
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.scheduler.Task
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

abstract class StandardModule<C : ModuleConfig> : AbstractModule<C>() {

    private val plugin: NicoYazawa
    val logger: Logger

    val moduleId: String
    val moduleName: String

    private lateinit var services: List<AbstractService<*>>
    private lateinit var commands: List<AbstractCommand<*, *>>
    private lateinit var runnables: List<Task>

    private lateinit var packageName: String

    init {
        val module = javaClass.getAnnotation(RegisterModule::class.java)

        moduleId = module.id
        moduleName = module.name
        plugin = NicoYazawa.getPlugin()

        logger = plugin.getLogger("Module", javaClass.simpleName)
    }

    final override fun preEnable() {
        logger.debug("Starting PreEnable")
        this.packageName = this.javaClass.getPackage().name + "."

        loadServices()
        performPreEnable()
        logger.debug("Finished PreEnable")
    }


    private fun loadServices() {
        logger.info("Loading Services")

        val services = getStreamForModule(AbstractService::class).toSet()

        val builder = ServiceBuilder(this.plugin, this)
        this.services = services.map { builder.register(it) }

        logger.debug("Finished Loading Services")
    }

    @Throws(Exception::class)
    protected open fun performPreEnable() {
    }

    final override fun onEnable() {
        logger.debug("Starting Enable")
        services.forEach { it.onEnable() }

        loadCommands()
        loadListeners()
        loadRunnables()
        performEnable()

        logger.debug("Finished Enable")
    }

    private fun loadCommands() {
        logger.info("Loading Commands")

        val builder = CommandBuilder(this.plugin, this)
        val classes = getStreamForModule(AbstractCommand::class).filter { it.findAnnotation<RegisterCommand>() != null }.toSet()

        commands = classes.map { builder.buildCommand(it, it.findAnnotation<RegisterCommand>()?.subCommandOf == AbstractCommand::class) }
        val registered = commands.flatMap { it.registerChildren(commands) }

        val orphans = commands.minus(registered).filter { !it.isRoot }
        if (orphans.isNotEmpty()) {
            orphans.forEach {
                Sponge.getEventManager().post(RegisterCommandRequestEvent(it, it::class.findAnnotation<RegisterCommand>()!!.subCommandOf, Cause.of(EventContext.empty(), this)))
            }
        }

        logger.debug("Finished Loading Commands")
    }


    private fun loadListeners() {
        logger.info("Loading Listeners")

        val events = getStreamForModule(AbstractListener::class).filter { it.findAnnotation<RegisterListener>() != null }.toSet()

        val builder = ListenerBuilder(plugin, this)
        events.forEach { builder.registerListener(it) }

        logger.debug("Finished Loading Listeners")
    }

    private fun loadRunnables() {
        logger.info("Loading Runnables")

        val runnables = getStreamForModule(AbstractRunnable::class).filter { it.findAnnotation<RegisterRunnable>() != null }

        val builder = RunnableBuilder(plugin, this)
        this.runnables = runnables.map { builder.register(it) }

        logger.debug("Finished Loading Runnables")
    }

    private fun <T : Any> getStreamForModule(assignable: KClass<T>): List<KClass<out T>> {
        return getStream(assignable).filter { it.java.`package`.name.startsWith(this.packageName) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getStream(assignable: KClass<T>): List<KClass<out T>> {
        return NicoYazawa.getPlugin().getModuleContainer().getLoadedClasses()
                .filter { it.isSubclassOf(assignable) }
                .filter { !it.isAbstract && !it.isInterface }
                .map { it as KClass<out T> }
    }

    @Throws(Exception::class)
    protected open fun performEnable() {
    }

    final override fun postEnable() {
        logger.debug("Starting PostEnable")
        deregisterCommandListeners()
        performPostEnable()
        logger.debug("Finished PostEnable")
    }

    // Remove RegisterCommandRequestEvent no more CrossModuleCommands can be added
    private fun deregisterCommandListeners() {
        commands.forEach {
            Sponge.getEventManager().unregisterListeners(it)
        }
    }

    @Throws(Exception::class)
    protected open fun performPostEnable() {
    }
}