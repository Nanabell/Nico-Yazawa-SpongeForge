package com.nanabell.sponge.nico.link.sync

enum class TroopSource {
    MINECRAFT,
    DISCORD;

    fun other(): TroopSource {
        return if (this == MINECRAFT) DISCORD else this
    }
}