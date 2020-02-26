package com.nanabell.sponge.nico.internal.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.MissingEventListenersException
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class ListenerBuilder (
        private val plugin: NicoYazawa,
        private val module: StandardModule
) {

    fun <E : AbstractListener<*>> registerListener(clazz: KClass<out E>) {
        val event = clazz.createInstance()

        if (clazz.members.all { it.findAnnotation<Listener>() == null })
            throw MissingEventListenersException(clazz)

        module.logger.info("Registered Listener: ${clazz.simpleName}") // TODO: change back to debug
        event.setModule(module)
        Sponge.getEventManager().registerListeners(plugin, event)
    }

}