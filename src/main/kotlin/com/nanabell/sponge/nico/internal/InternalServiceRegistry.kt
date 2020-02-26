package com.nanabell.sponge.nico.internal

import kotlin.reflect.KClass

class InternalServiceRegistry {

    private val services: MutableMap<KClass<*>, Any> = HashMap()

    fun <C : Any, S : C> register(clazz: KClass<out C>, service: S) {
        register(clazz, service, false)
    }

    fun <C : Any, S : C> register(clazz: KClass<out C>, service: S, override: Boolean) {
        if (!override && services.containsKey(clazz)) return

        services[clazz] = service
    }

    fun isRegistered(clazz: KClass<*>): Boolean {
        return services.containsKey(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Any> provide(clazz: KClass<C>): C? {
        if (!services.containsKey(clazz)) return null

        return services[clazz] as C
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Any> provideUnchecked(clazz: KClass<C>): C {
        if (!services.containsKey(clazz)) throw NoSuchElementException("Service '$clazz' has not been registered!")

        return services[clazz] as C
    }

}