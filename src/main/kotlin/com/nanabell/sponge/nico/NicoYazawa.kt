package com.nanabell.sponge.nico

import com.nanabell.quickstart.container.DiscoveryModuleContainer
import com.nanabell.sponge.nico.internal.InternalServiceRegistry
import com.nanabell.sponge.nico.internal.PermissionRegistry
import com.nanabell.sponge.nico.internal.interfaces.Reloadable


abstract class NicoYazawa {

    abstract fun getLogger(vararg topics: String): TopicLogger

    abstract fun registerReloadable(reloadable: Reloadable)

    abstract fun getPermissionRegistry(): PermissionRegistry

    abstract fun getModuleContainer(): DiscoveryModuleContainer

    abstract fun getServiceRegistry(): InternalServiceRegistry

    companion object {
        private lateinit var nicoYazawa: NicoYazawa

        fun setPlugin(nicoYazawa: NicoYazawa) {
            this.nicoYazawa = nicoYazawa
        }

        fun getPlugin(): NicoYazawa {
            return nicoYazawa
        }

        fun registerReloadable(reloadable: Reloadable) {
            getPlugin().registerReloadable(reloadable)
        }

        fun getServiceRegistry(): InternalServiceRegistry {
            return getPlugin().getServiceRegistry()
        }
    }
}