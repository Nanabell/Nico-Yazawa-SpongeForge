package com.nanabell.sponge.nico.internal.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.ApiService
import com.nanabell.sponge.nico.internal.annotation.RegisterService
import com.nanabell.sponge.nico.internal.module.ConfigurableModule
import com.nanabell.sponge.nico.internal.module.StandardModule
import org.spongepowered.api.Sponge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class ServiceBuilder(
        private val plugin: NicoYazawa,
        private val module: StandardModule
) {

    private val logger = NicoYazawa.getPlugin().getLogger("ServiceBuilder")

    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractService<out ConfigurableModule<*>>> register(clazz: KClass<T>) {
        val rs = clazz.findAnnotation<RegisterService>()
        if (rs == null) {
            logger.warn("Service Class {} is not Annotated with required Annotation @RegisterService", clazz)
            return
        }
        val key = if (rs.value == AbstractService::class) clazz else rs.value

        val service: T = clazz.createInstance()
        if (!service::class.isSubclassOf(rs.value)) {
            logger.error("Service {} is not a subclass of {}", service::class, rs.value)
            return
        }

        if (clazz.findAnnotation<ApiService>() != null) {
            if (Sponge.getServiceManager().isRegistered(key.java as Class<Any>) && !rs.override) {
                logger.warn("There already is a Service registered for {}", rs.value)
                return
            }

            Sponge.getServiceManager().setProvider(plugin, rs.value.java as Class<Any>, service)
        } else {
            if (plugin.getServiceRegistry().isRegistered(rs.value) && !rs.override) {
                logger.warn("There already is an Internal Service registered for {}", rs.value)
                return
            }

            plugin.getServiceRegistry().register(rs.value, service)
        }

        service.setModule(module)
        service.onEnable()
    }
}