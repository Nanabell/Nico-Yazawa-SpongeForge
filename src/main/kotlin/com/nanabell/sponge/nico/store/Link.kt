package com.nanabell.sponge.nico.store

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.extensions.orNull
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Indexed
import net.dv8tion.jda.api.JDA
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

@Entity("Discord.Link", noClassnameStored = true)
data class Link(
        @Indexed
        val discordId: Long,

        @Indexed
        val minecraftId: UUID
) {
    private constructor() : this(-1, UUID(-1, -1))

    fun fetchUser(jda: JDA): DiscordUser? {
        return jda.getUserById(discordId)
    }

    fun fetchUser(userStorage: UserStorageService): MinecraftUser? {
        return userStorage[minecraftId].orNull()
    }
}
