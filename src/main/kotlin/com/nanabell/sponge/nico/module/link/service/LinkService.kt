package com.nanabell.sponge.nico.module.link.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.LinkResult
import com.nanabell.sponge.nico.module.link.database.Link
import com.nanabell.sponge.nico.module.link.event.LinkedEvent
import com.nanabell.sponge.nico.module.link.event.UnlinkedEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*
import kotlin.collections.HashMap

@RegisterService
class LinkService : AbstractService<LinkModule>() {

    private val eventManager = Sponge.getEventManager()
    private val databaseService = NicoYazawa.getServiceRegistry().provideUnchecked(DatabaseService::class)

    private val pendingLinks = HashMap<MinecraftUser, DiscordUser>()
    private val cacheLinks = Caffeine.newBuilder().build<UUID, Link> { databaseService.findById("minecraftId", it) }

    override fun onPreEnable() {

    }

    fun isPending(user: DiscordUser): Boolean {
        return pendingLinks.containsValue(user)
    }

    fun addPending(discordUser: DiscordUser, minecraftUser: MinecraftUser): Boolean {
        return pendingLinks.putIfAbsent(minecraftUser, discordUser) == null
    }

    fun removePending(user: MinecraftUser): Boolean {
        return pendingLinks.remove(user) != null
    }

    fun isLinked(user: MinecraftUser): Boolean {
        return cacheLinks[user.uniqueId] != null
    }

    fun getLink(user: MinecraftUser): Link? {
        return cacheLinks[user.uniqueId]
    }

    fun link(dUser: DiscordUser, mUser: MinecraftUser): LinkResult {
        if (isLinked(mUser)) return LinkResult.ALREADY_LINKED

        addPending(dUser, mUser)
        return confirmLink(mUser)
    }

    fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.ALREADY_LINKED
        val discordUser = pendingLinks.remove(user) ?: return LinkResult.NO_LINK_REQUEST

        val link = Link(discordUser.idLong, user.uniqueId)
        databaseService.save(link).also { cacheLinks.put(user.uniqueId, link) }

        eventManager.post(LinkedEvent(user, discordUser, Cause.of(EventContext.empty(), this)))
        return LinkResult.LINKED
    }

    fun unlink(user: MinecraftUser): LinkResult {
        val link: Link = databaseService.findAndDelete("minecraftId", user.uniqueId) ?: return LinkResult.NOT_LINKED
        cacheLinks.invalidate(user.uniqueId)

        eventManager.post(UnlinkedEvent(user.uniqueId, link.discordId, Cause.of(EventContext.empty(), this)))
        return LinkResult.UNLINKED
    }
}