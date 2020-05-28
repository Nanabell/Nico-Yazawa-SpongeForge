package com.nanabell.sponge.nico.module.link.database

import com.nanabell.sponge.nico.internal.database.DataEntry
import com.nanabell.sponge.nico.internal.extension.DiscordUser
import com.nanabell.sponge.nico.internal.extension.MinecraftUser
import com.nanabell.sponge.nico.internal.extension.toDiscordUser
import com.nanabell.sponge.nico.internal.extension.toMinecraftUser
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import dev.morphia.annotations.Indexed
import java.util.*

@Entity("Discord.Link", noClassnameStored = true)
data class Link(

        @Id
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
