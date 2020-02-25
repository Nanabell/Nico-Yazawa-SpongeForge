package com.nanabell.sponge.nico.internal.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.ApiService
import com.nanabell.sponge.nico.internal.annotation.RegisterService
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class ServiceBuilder(
        private val plugin: NicoYazawa,
        private val logger: Logger
) {

    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractService> register(clazz: KClass<T>) {
        val rs = clazz.findAnnotation<RegisterService>()
        if (rs == null) {
            logger.warn("Service Class {} is not Annotated with required Annotation @RegisterService", clazz)
            return
        }

        val service = clazz.createInstance()
        if (!service::class.isSubclassOf(rs.value)) {
            logger.error("Service {} is not a subclass of {}", service::class, rs.value)
            return
        }

        if (clazz.findAnnotation<ApiService>() != null) {
            if (Sponge.getServiceManager().isRegistered(rs.value.java) && !rs.override) {
                logger.warn("There already is a Service registered for {}", rs.value)
                return
            }

            Sponge.getServiceManager().setProvider(plugin, rs.value.java, service)
        } else {
            if (plugin.getServiceRegistry().isRegistered(rs.value) && !rs.override) {
                logger.warn("There already is an Internal Service registered for {}", rs.value)
                return
            }

            plugin.getServiceRegistry().register(rs.value, service)
        }
    }
}