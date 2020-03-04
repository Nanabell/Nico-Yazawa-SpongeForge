package com.nanabell.sponge.nico.internal.extension

import com.nanabell.sponge.nico.internal.MissingAnnotationException
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf

val KClass<*>.isInterface get() = java.isInterface

inline fun <reified S : Any> KClass<out S>.getActualTypeArguments(typedSuperclass: KClass<S>): List<KClass<*>> {
    if (!typedSuperclass.isSuperclassOf(this))
        return emptyList()

    val type = typedSuperclass.java.genericSuperclass
    return if (type is ParameterizedType) type.actualTypeArguments.map { (it as Class<*>).kotlin } else emptyList()
}

fun KClass<out AbstractCommand<*, *>>.getSubCommandPath(): String {
    val builder = StringBuilder()

    getNextSubCommandPath(this, builder, false)
    return builder.toString()
}

private fun getNextSubCommandPath(clazz: KClass<out AbstractCommand<*, *>>, builder: StringBuilder, appendPeriod: Boolean) {
    val co = clazz.findAnnotation<RegisterCommand>() ?: throw MissingAnnotationException(clazz, RegisterCommand::class)
    if (!co.subCommandOf.isAbstract && co.subCommandOf.java != clazz)
        getNextSubCommandPath(co.subCommandOf, builder, true)

    // Special handling for nico!
    if (co.value[0] != "nico") {
        builder.append(co.value[0])

        if (appendPeriod) {
            builder.append('.')
        }
    }
}