package com.nanabell.sponge.nico.module.sync.misc

enum class TroopSource {
    MINECRAFT,
    DISCORD;

    fun other(): TroopSource {
        return if (this == MINECRAFT) DISCORD else MINECRAFT
    }
}