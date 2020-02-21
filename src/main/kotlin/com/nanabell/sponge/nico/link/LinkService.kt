package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import com.nanabell.sponge.nico.store.Link
import dev.morphia.Datastore
import dev.morphia.query.Query
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import java.util.*

class LinkService(private val plugin: NicoYazawa) {

    private val dataSource = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)
    private val discordService by lazy { Sponge.getServiceManager().provideUnchecked(DiscordService::class.java) }
    private val eventManager = Sponge.getEventManager()

    private val pendingLinks = HashBiMap.create<Long, UUID>()

    fun init() {
        Sponge.getEventManager().registerListeners(plugin, LinkListener())
    }

    fun isPending(user: DiscordUser): Boolean {
        return pendingLinks.containsKey(user.idLong)
    }

    fun isPending(user: MinecraftUser): Boolean {
        return pendingLinks.containsValue(user.uniqueId)
    }

    fun addPending(discordUser: DiscordUser, minecraftUser: MinecraftUser): Boolean {
        return pendingLinks.putIfAbsent(discordUser.idLong, minecraftUser.uniqueId) == null
    }

    fun removePending(user: MinecraftUser): Boolean {
        return pendingLinks.inverse().remove(user.uniqueId) != null
    }

    fun isLinked(user: MinecraftUser): Boolean {
        return getLink(user) != null
    }

    fun getLink(user: MinecraftUser): Link? {
        return getQuery(user.uniqueId).first()
    }

    fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.error(LinkState.ALREADY_LINKED)
        val discordId = pendingLinks.inverse().remove(user.uniqueId) ?: return LinkResult.error(LinkState.NO_LINK_REQUEST)
        val dUser = discordService.getUserById(discordId) ?: return LinkResult.error(LinkState.USER_NOT_FOUND)

        dataSource.save(Link(discordId, user.uniqueId))

        val cause = Cause.of(EventContext.of(mapOf(LinkEventContextKeys.DISCORD_USER to dUser, LinkEventContextKeys.MINECRAFT_USER to user)), this)
        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, cause))

        return LinkResult.success(discordId, user.uniqueId)
    }

    fun link(dUser: DiscordUser, mUser: MinecraftUser): LinkResult {
        if (isLinked(mUser)) return LinkResult.error(LinkState.ALREADY_LINKED)

        addPending(dUser, mUser)
        return confirmLink(mUser)
    }

    fun unlink(user: MinecraftUser): LinkResult {
        val link = dataSource.findAndDelete(getQuery(user.uniqueId)) ?: return LinkResult.error(LinkState.NOT_LINKED)
        val dUser =  link.fetchUser(discordService.jda)

        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, Cause.of(EventContext.of(mapOf(LinkEventContextKeys.DISCORD_USER to dUser, LinkEventContextKeys.MINECRAFT_USER to user)), this)))
        return LinkResult(LinkState.UNLINKED, null)
    }

    private fun getQuery(minecraftId: UUID): Query<Link> {
        return dataSource.createQuery(Link::class.java).field("minecraftId").equal(minecraftId)
    }

}