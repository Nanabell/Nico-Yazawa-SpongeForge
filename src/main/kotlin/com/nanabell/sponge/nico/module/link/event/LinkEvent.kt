package com.nanabell.sponge.nico.module.link.event

import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.internal.event.StandardEvent
import com.nanabell.sponge.nico.module.link.LinkResult
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause
import java.util.*

open class LinkEvent(cause: Cause) : StandardEvent(cause)
class LinkRequestEvent(val username: String, val source: DiscordUser, cause: Cause) : LinkEvent(cause), Event
class UsernameFoundEvent(val username: String, val source: DiscordUser, cause: Cause) : LinkEvent(cause)

open class LinkRequestFailedEvent(val source: DiscordUser, cause: Cause) : LinkEvent(cause)
class UsernameNotFoundEvent(val username: String, source: DiscordUser, cause: Cause) : LinkRequestFailedEvent(source, cause)
class AlreadyPendingEvent(source: DiscordUser, cause: Cause) : LinkRequestFailedEvent(source, cause)
class AlreadyLinkedEvent(val target: MinecraftUser, source: DiscordUser, cause: Cause) : LinkRequestFailedEvent(source, cause)


open class LinkStateChangeEvent(val uniqueId: UUID, val userId: Long, val state: LinkResult, cause: Cause) : LinkEvent(cause)
class LinkedEvent(val minecraftUser: MinecraftUser, val discordUser: DiscordUser, cause: Cause) : LinkStateChangeEvent(minecraftUser.uniqueId, discordUser.idLong, LinkResult.LINKED, cause)
class UnlinkedEvent(uniqueId: UUID, userId: Long,  cause: Cause) : LinkStateChangeEvent(uniqueId, userId, LinkResult.UNLINKED, cause)
