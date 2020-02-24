package com.nanabell.sponge.nico.internal

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.command.CommandPermissionHandler
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.permission.PermissionService
import kotlin.reflect.KClass

class PermissionRegistry {

    private var initialized = false
    private val commandPermissionRegistry: MutableMap<KClass<out AbstractCommand<*, *>>, CommandPermissionHandler> = HashMap()

    fun getHandler(clazz: KClass<out AbstractCommand<*, *>>): CommandPermissionHandler {
        if (commandPermissionRegistry.containsKey(clazz)) {
            return commandPermissionRegistry[clazz]!!
        }


        commandPermissionRegistry[clazz] = CommandPermissionHandler(clazz)
        return getHandler(clazz)
    }


    fun registerPermissions() {
        if (initialized) return
        initialized = true

        val permissionService = Sponge.getServiceManager().provide(PermissionService::class.java).orNull()
        if (permissionService != null) {

            commandPermissionRegistry.forEach { entry ->
                entry.value.getPermissions().forEach { permission ->
                    permissionService.newDescriptionBuilder(NicoYazawa.getPlugin()).id(permission).register() // TODO: Add description
                }
            }
        }
    }
}