package com.nanabell.sponge.nico.internal.module

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.command.CommandBuilder
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.internal.service.ServiceBuilder
import org.slf4j.Logger
import uk.co.drnaylor.quickstart.Module
import uk.co.drnaylor.quickstart.annotations.ModuleData
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

abstract class StandardModule : Module {

    val plugin: NicoYazawa
    val logger: Logger

    val moduleId: String
    val moduleName: String

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
        logger.info("Starting PreEnable")
        this.packageName = this.javaClass.getPackage().name + "."

        loadServices()
        performPreEnable()
        logger.info("Finished PreEnable")
    }


    private fun loadServices() {
        logger.debug("Loading Services")

        val services = getStreamForModule(AbstractService::class).toSet()

        val builder = ServiceBuilder(this.plugin, this)
        services.forEach { builder.register(it) }

        logger.debug("Finished Loading Services")
    }

    @Throws(Exception::class)
    protected open fun performPreEnable() {
    }

    final override fun onEnable() {
        logger.info("Starting Enable")
        loadCommands()
        loadEvents()
        loadRunnables()
        performEnable()
        logger.info("Finished Enable")
    }

    private fun loadCommands() {
        logger.debug("Loading Commands")

        val commands = getStreamForModule(AbstractCommand::class).filter { it.findAnnotation<RegisterCommand>() != null }.toSet()
        val baseCommands = commands.filter { it.findAnnotation<RegisterCommand>()?.subCommandOf == AbstractCommand::class }

        val commandBuilder = CommandBuilder(this.plugin, commands, this)
        baseCommands.forEach { commandBuilder.buildCommand(it) }

        logger.debug("Finished Loading Commands")
    }

    private fun loadEvents() {
        logger.info("Loading Events")
    }

    private fun loadRunnables() {
        logger.info("Loading Runnables")
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
        logger.info("Starting PostEnable")
        performPostEnable()
        logger.info("Finished PostEnable")
    }

    @Throws(Exception::class)
    protected open fun performPostEnable() {
    }
}