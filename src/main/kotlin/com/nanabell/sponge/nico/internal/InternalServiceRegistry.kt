package com.nanabell.sponge.nico.internal

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
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

    fun <C : Any> provide(clazz: KClass<C>): C? {
        if (!isRegistered(clazz)) return null

        return services[clazz] as C
    }

    inline fun <reified C : Any> provide(): C? {
        if (!isRegistered(C::class)) return null

        return provide(C::class)
    }

    @Throws(NoSuchElementException::class)
    inline fun <reified C : Any> provideUnchecked(): C {
        if (!isRegistered(C::class)) throw NoSuchElementException("Service '${C::class}' has not been registered!")

        return provide(C::class)!!
    }
}