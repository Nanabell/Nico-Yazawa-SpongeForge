package com.nanabell.sponge.nico.internal.module

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.command.CommandBuilder
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.internal.listener.ListenerBuilder
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.internal.runnable.RunnableBuilder
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.internal.service.ServiceBuilder
import org.slf4j.Logger
import org.spongepowered.api.scheduler.Task
import uk.co.drnaylor.quickstart.Module
import uk.co.drnaylor.quickstart.annotations.ModuleData
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

abstract class StandardModule : Module {

    private val plugin: NicoYazawa
    val logger: Logger

    val moduleId: String
    val moduleName: String

    private lateinit var services: List<AbstractService<*>>
    private lateinit var runnables: List<Task>

    private lateinit var packageName: String

    init {
        val module = javaClass.getAnnotation(ModuleData::class.java)
        this.configAdapter

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

        val commands = getStreamForModule(AbstractCommand::class).filter { it.findAnnotation<RegisterCommand>() != null }.toSet()
        val baseCommands = commands.filter { it.findAnnotation<RegisterCommand>()?.subCommandOf == AbstractCommand::class }

        val commandBuilder = CommandBuilder(this.plugin, commands, this)
        baseCommands.forEach { commandBuilder.buildCommand(it) }

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

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getStreamForModule(assignable: KClass<T>): List<KClass<out T>> {
        val loaded = NicoYazawa.getPlugin().getModuleContainer().loadedClasses
        return loaded
                .filter { it.kotlin.isSubclassOf(assignable) }
                .filter { it.`package`.name.startsWith(this.packageName) }
                .filter { !Modifier.isAbstract(it.modifiers) && !Modifier.isInterface(it.modifiers) }
                .map { it.kotlin as KClass<out T> }
    }

    @Throws(Exception::class)
    protected open fun performEnable() {
    }

    final override fun postEnable() {
        logger.debug("Starting PostEnable")
        performPostEnable()
        logger.debug("Finished PostEnable")
    }

    @Throws(Exception::class)
    protected open fun performPostEnable() {
    }
}