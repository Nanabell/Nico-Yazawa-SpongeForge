package com.nanabell.sponge.nico

import com.nanabell.sponge.nico.internal.InternalServiceRegistry
import com.nanabell.sponge.nico.internal.PermissionRegistry
import uk.co.drnaylor.quickstart.modulecontainers.DiscoveryModuleContainer


abstract class NicoYazawa {

    abstract fun getLogger(vararg topics: String): TopicLogger

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

        fun getServiceRegistry(): InternalServiceRegistry {
            return getPlugin().getServiceRegistry()
        }
    }
}