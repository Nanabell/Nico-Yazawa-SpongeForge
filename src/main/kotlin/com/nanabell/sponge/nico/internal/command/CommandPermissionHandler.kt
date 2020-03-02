package com.nanabell.sponge.nico.internal.command

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.extension.getSubCommandPath
import org.spongepowered.api.service.permission.Subject
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class CommandPermissionHandler(clazz: KClass<out AbstractCommand<*, *>>) {

    private val isIgnored: Boolean

    private val prefix: String
    private val base: String
    private val others: String

    private val permissions: MutableList<String> = ArrayList()

    init {
        val p = clazz.findAnnotation<Permissions>()
        if (p != null) {
            val builder = StringBuilder(NicoConstants.PERMISSION_PREFIX)

            if (p.prefix.isNotEmpty()) builder.append(p.prefix).append('.')

            if (p.mainOverride.isNotEmpty()) builder.append(p.mainOverride) else builder.append(clazz.getSubCommandPath())
            builder.append('.')

            if (p.suffix.isNotEmpty()) builder.append(p.suffix).append('.')

            this.prefix = builder.toString()
            this.base = prefix + "base"
            this.others = prefix + "others"

            // Build Permission [Map]
            this.permissions.add(this.base)

            if (p.supportsOthers)
                this.permissions.add(this.others)

            isIgnored = false
        } else {

            // No Annotation found. Assuming no permission requirement
            this.isIgnored = true
            this.prefix = ""
            this.base = ""
            this.others = ""
        }
    }

    fun getPermissions(): List<String> {
        return this.permissions
    }

    fun getBase(): String {
        return this.base
    }

    fun getOthers(): String {
        return this.others
    }

    fun registerPermission(permission: String) {
        this.permissions.add(permission)
    }

    fun registerSuffixPermission(permission: String) {
        this.permissions.add(this.prefix + permission)
    }

    fun checkBase(src: Subject): Boolean {
        return testSubject(src, this.base)
    }

    fun checkOthers(src: Subject): Boolean {
        return testSubject(src, this.others)
    }

    private fun testSubject(src: Subject, permission: String): Boolean {
        return isIgnored || src.hasPermission(permission)
    }
}