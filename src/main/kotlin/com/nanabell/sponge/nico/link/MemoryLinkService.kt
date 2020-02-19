package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import java.util.*

class MemoryLinkService : LinkService {

    private val eventManager = Sponge.getEventManager()

    private val pendingLinks = HashBiMap.create<Long, UUID>()
    private val links = HashBiMap.create<Long, UUID>()


    override fun isPending(user: DiscordUser): Boolean {
        return pendingLinks.containsKey(user.idLong)
    }

    override fun isPending(user: MinecraftUser): Boolean {
        return pendingLinks.containsValue(user.uniqueId)
    }

    override fun addPending(discordUser: DiscordUser, minecraftUser: MinecraftUser): Boolean {
        return pendingLinks.putIfAbsent(discordUser.idLong, minecraftUser.uniqueId) == null
    }

    override fun isLinked(user: MinecraftUser): Boolean {
        return links.containsValue(user.uniqueId)
    }

    override fun getLink(user: MinecraftUser): Link? {
        if (!isLinked(user)) return null

        return Link(links.inverse()[user.uniqueId]!!, user.uniqueId)
    }

    override fun removePending(user: MinecraftUser): Boolean {
        return pendingLinks.inverse().remove(user.uniqueId) != null
    }

    override fun confirmLink(user: MinecraftUser, cause: Cause): LinkResult {
        if (isLinked(user)) return LinkResult.error(LinkState.ALREADY_LINKED)
        if (!isPending(user)) return LinkResult.error(LinkState.NO_LINK_REQUEST)

        links.inverse()[user.uniqueId] = pendingLinks.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, cause))

        return LinkResult.success(links.inverse()[user.uniqueId]!!, user.uniqueId)
    }

    override fun unlink(user: MinecraftUser, cause: Cause): LinkResult {
        if (!isLinked(user)) return LinkResult.error(LinkState.UNLINKED)

        val id = links.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, cause))

        return LinkResult.success(LinkState.UNLINKED, id!!, user.uniqueId)
    }
}