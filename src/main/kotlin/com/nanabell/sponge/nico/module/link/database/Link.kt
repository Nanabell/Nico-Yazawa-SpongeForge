package com.nanabell.sponge.nico.module.link.database

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.extensions.toDiscordUser
import com.nanabell.sponge.nico.extensions.toMinecraftUser
import com.nanabell.sponge.nico.internal.database.DataEntry
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import java.util.*

@Entity("Discord.Link", noClassnameStored = true)
data class Link(
        @Indexed
        val discordId: Long,

        @Indexed
        val minecraftId: UUID

) : DataEntry {
    private constructor() : this(-1, UUID(-1, -1))

    fun fetchDiscordUser(): DiscordUser? {
        return discordId.toDiscordUser()
    }

    fun fetchMinecraftUser(): MinecraftUser? {
        return minecraftId.toMinecraftUser()
    }
}