package com.nanabell.sponge.nico

import com.nanabell.sponge.nico.config.Config
import com.nanabell.sponge.nico.config.MainConfig


abstract class NicoYazawa {

    abstract fun getLogger(vararg topics: String): TopicLogger

    abstract fun getConfig(): Config<MainConfig>

    companion object {
        private lateinit var nicoYazawa: NicoYazawa

        fun setPlugin(nicoYazawa: NicoYazawa) {
            this.nicoYazawa = nicoYazawa
        }

        fun getPlugin(): NicoYazawa {
            return nicoYazawa
        }
    }
}