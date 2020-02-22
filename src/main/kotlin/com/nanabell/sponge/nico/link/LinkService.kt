package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.discord.DiscordService
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import com.nanabell.sponge.nico.store.Link
import dev.morphia.Datastore
import dev.morphia.query.Query
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.cause.EventContextKeys
import java.util.*

class LinkService {

    private val dataSource = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)
    private val discordService by lazy { Sponge.getServiceManager().provideUnchecked(DiscordService::class.java) }
    private val eventManager = Sponge.getEventManager()

    private val pendingLinks = HashBiMap.create<MinecraftUser, DiscordUser>()

    fun init() {
        Sponge.getEventManager().registerListeners(NicoYazawa.getPlugin(), LinkListener())
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
        return getLink(user) != null
    }

    fun getLink(user: MinecraftUser): Link? {
        return getQuery(user.uniqueId).first()
    }

    fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.error(LinkState.ALREADY_LINKED)
        val discordUser = pendingLinks.remove(user) ?: return LinkResult.error(LinkState.NO_LINK_REQUEST)

        dataSource.save(Link(discordUser.idLong, user.uniqueId))

        val cause = Cause.of(EventContext.of(mapOf(NicoConstants.DISCORD_USER to discordUser, EventContextKeys.OWNER to user)), this)
        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, cause))

        return LinkResult.success(discordUser.idLong, user.uniqueId)
    }

    fun link(dUser: DiscordUser, mUser: MinecraftUser): LinkResult {
        if (isLinked(mUser)) return LinkResult.error(LinkState.ALREADY_LINKED)

        addPending(dUser, mUser)
        return confirmLink(mUser)
    }

    fun unlink(user: MinecraftUser): LinkResult {
        val link = dataSource.findAndDelete(getQuery(user.uniqueId)) ?: return LinkResult.error(LinkState.NOT_LINKED)
        val dUser =  link.fetchUser(discordService.jda)

        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, Cause.of(EventContext.of(mapOf(NicoConstants.DISCORD_USER to dUser, EventContextKeys.OWNER to user)), this)))
        return LinkResult(LinkState.UNLINKED, null)
    }

    private fun getQuery(minecraftId: UUID): Query<Link> {
        return dataSource.createQuery(Link::class.java).field("minecraftId").equal(minecraftId)
    }

}