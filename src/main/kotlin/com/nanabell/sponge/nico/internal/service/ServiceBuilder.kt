package com.nanabell.sponge.nico.internal.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.InvalidSubClassException
import com.nanabell.sponge.nico.internal.MissingAnnotationException
import com.nanabell.sponge.nico.internal.annotation.service.ApiService
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.spongepowered.api.Sponge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class ServiceBuilder(
        private val plugin: NicoYazawa,
        private val module: StandardModule<*>
) {

    private val logger = module.logger

    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractService<*>> register(clazz: KClass<T>): T {
        val rs = clazz.findAnnotation<RegisterService>()
                ?: throw MissingAnnotationException(clazz, RegisterService::class)

        val key = if (rs.value == AbstractService::class) clazz else rs.value

        val service: T = clazz.createInstance()
        if (!service::class.isSubclassOf(rs.value)) throw InvalidSubClassException(clazz, rs.value)

        try {
            service.setModule(module)
            service.onPreEnable()

            if (clazz.findAnnotation<ApiService>() != null) {
                if (Sponge.getServiceManager().isRegistered(key.java as Class<Any>) && !rs.override) {
                    logger.warn("There already is a Service registered for {}", key)
                    TODO("Get a proper exception for this")
                }
                Sponge.getServiceManager().setProvider(plugin, key.java as Class<Any>, service)
            }

            if (plugin.getServiceRegistry().isRegistered(key) && !rs.override) {
                logger.warn("There already is an Internal Service registered for {}", key)
                TODO("Get a proper exception for this")
            }
            plugin.getServiceRegistry().register(key, service)

            logger.debug("Registered Service: ${clazz.simpleName}")
        } catch (e: Exception) {
            logger.error("Failed to construct Service: $clazz", e)
        }

        return service
    }
}