package com.nanabell.sponge.nico

import com.nanabell.quickstart.container.DiscoveryModuleContainer
import com.nanabell.sponge.nico.internal.InternalServiceRegistry
import com.nanabell.sponge.nico.internal.PermissionRegistry
import com.nanabell.sponge.nico.internal.interfaces.Reloadable
import org.quartz.Scheduler


abstract class NicoYazawa {

    abstract fun getLogger(vararg topics: String): TopicLogger

    abstract fun getScheduler(): Scheduler

    abstract fun getPermissionRegistry(): PermissionRegistry

    abstract fun getModuleContainer(): DiscoveryModuleContainer

    abstract fun getServiceRegistry(): InternalServiceRegistry

    abstract fun registerReloadable(reloadable: Reloadable)

    companion object {
        private lateinit var nicoYazawa: NicoYazawa

        fun setPlugin(nicoYazawa: NicoYazawa) {
            this.nicoYazawa = nicoYazawa
        }

        fun getPlugin(): NicoYazawa {
            return nicoYazawa
        }

        fun getScheduler(): Scheduler {
            return getPlugin().getScheduler()
        }

        fun getServiceRegistry(): InternalServiceRegistry {
            return getPlugin().getServiceRegistry()
        }

        fun registerReloadable(reloadable: Reloadable) {
            getPlugin().registerReloadable(reloadable)
        }
    }
}