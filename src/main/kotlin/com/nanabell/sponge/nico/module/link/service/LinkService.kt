package com.nanabell.sponge.nico.module.link.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.extension.DiscordUser
import com.nanabell.sponge.nico.internal.extension.MinecraftUser
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.database.Link
import com.nanabell.sponge.nico.module.link.event.LinkedEvent
import com.nanabell.sponge.nico.module.link.event.UnlinkedEvent
import com.nanabell.sponge.nico.module.link.misc.LinkResult
import com.nanabell.sponge.nico.module.link.store.LinkStore
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*
import kotlin.collections.HashMap

@RegisterService
class LinkService : AbstractService<LinkModule>() {

    private val pendingLinks = HashMap<MinecraftUser, DiscordUser>()
    private val cache = Caffeine.newBuilder().build<UUID, Link> { store.load(it) }
    private lateinit var store: LinkStore

    override fun onPreEnable() {
        store = LinkStore()
        reload()
    }

    // Pending Links

    fun isPending(minecraftUser: MinecraftUser): Boolean {
        return pendingLinks.containsKey(minecraftUser)
    }

    fun isPending(user: DiscordUser): Boolean {
        return pendingLinks.containsValue(user)
    }

    fun addPending(minecraftUser: MinecraftUser, discordUser: DiscordUser): Boolean {
        return pendingLinks.putIfAbsent(minecraftUser, discordUser) == null
    }

    fun removePending(user: MinecraftUser): Boolean {
        return pendingLinks.remove(user) != null
    }

    fun removePending(user: DiscordUser): Boolean {
        pendingLinks.forEach {
            if (it.value.idLong == user.idLong) return pendingLinks.remove(it.key) != null
        }

        return false
    }

    // Linking

    fun isLinked(user: MinecraftUser): Boolean {
        return cache[user.uniqueId] != null
    }

    fun isLinked(user: DiscordUser): Boolean {
        return cache.asMap().values.any { it.discordId == user.idLong }
    }

    fun getLink(user: MinecraftUser): Link? {
        return cache[user.uniqueId]
    }

    fun getLink(user: DiscordUser): Link? {
        return cache.asMap().values.firstOrNull { it.discordId == user.idLong }
    }

    fun link(dUser: DiscordUser, mUser: MinecraftUser): LinkResult {
        if (isLinked(mUser)) return LinkResult.ALREADY_LINKED

        addPending(mUser, dUser)
        return confirmLink(mUser)
    }

    fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.ALREADY_LINKED
        val discordUser = pendingLinks.remove(user) ?: return LinkResult.NO_LINK_REQUEST

        val link = Link(discordUser.idLong, user.uniqueId)
        store.save(link).also { cache.put(user.uniqueId, link) }

        Sponge.getEventManager().post(LinkedEvent(user, discordUser, Cause.of(EventContext.empty(), this)))
        return LinkResult.LINKED
    }

    fun unlink(user: MinecraftUser): LinkResult {
        val link: Link = cache[user.uniqueId] ?: return LinkResult.NOT_LINKED
        cache.invalidate(user.uniqueId)

        store.remove(link)
        Sponge.getEventManager().post(UnlinkedEvent(user.uniqueId, link.discordId, Cause.of(EventContext.empty(), this)))
        return LinkResult.UNLINKED
    }

    private fun reload() {
        cache.invalidateAll()
        store.loadAll().forEach { cache.put(it.minecraftId, it) }
    }
}