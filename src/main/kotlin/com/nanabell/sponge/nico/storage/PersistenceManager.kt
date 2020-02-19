package com.nanabell.sponge.nico.storage

import java.util.*

class PersistenceManager {
    private val persistableMap: MutableMap<Class<out IdentifiableDaoEnabled<*>>, Persistable<*>> = HashMap()

    fun <T : IdentifiableDaoEnabled<T>> register(persistable: Persistable<T>) {
        persistableMap[persistable.daoClass] = persistable
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : IdentifiableDaoEnabled<T>> get(clazz: Class<T>): Persistable<T> {
        return persistableMap[clazz] as Persistable<T>
    }

    fun <T : IdentifiableDaoEnabled<T>> getUnchecked(clazz: Class<T>): Persistable<T> {
        return get(clazz)
    }
}