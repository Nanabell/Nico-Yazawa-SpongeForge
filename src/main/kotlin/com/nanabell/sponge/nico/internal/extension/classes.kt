package com.nanabell.sponge.nico.internal.extension

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

inline fun <reified S : Any> KClass<out S>.getActualTypeArguments(typedSuperclass: KClass<S>): List<KClass<*>> {
    if (!typedSuperclass.isSuperclassOf(this))
        return emptyList()

    val type = typedSuperclass.java.genericSuperclass
    return if (type is ParameterizedType) type.actualTypeArguments.map { (it as Class<*>).kotlin } else emptyList()
}