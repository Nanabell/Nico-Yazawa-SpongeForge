package com.nanabell.sponge.nico.internal.command

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.spongepowered.api.Sponge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class CommandBuilder(
        private val plugin: NicoYazawa,
        private val commandSet: Set<KClass<out AbstractCommand<*>>>,
        private val module: StandardModule
) {
    fun <T : AbstractCommand<*>> buildCommand(clazz: KClass<out T>): T {
        return buildCommand(clazz, true)
    }

    fun <T : AbstractCommand<*>> buildCommand(clazz: KClass<out T>, isRoot: Boolean): T {
        val command = clazz.createInstance()

        command.setModule(module)
        command.setModuleCommands(commandSet)
        command.setCommandBuilder(this)
        command.postInit()

        if (isRoot) {
            Sponge.getCommandManager().register(plugin, command, *command.aliases)
        }

        return command
    }
}