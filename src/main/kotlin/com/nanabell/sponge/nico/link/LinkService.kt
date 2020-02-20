package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.store.Link
import dev.morphia.Datastore
import dev.morphia.query.Query
import org.spongepowered.api.Sponge
import java.util.*

class LinkService {

    private val dataSource = Sponge.getServiceManager().provideUnchecked(Datastore::class.java)
    private val pendingLinks = HashBiMap.create<Long, UUID>()

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
        return getQuery(user.uniqueId).count() == 1L
    }

    fun getLink(user: MinecraftUser): Link? {
        return getQuery(user.uniqueId).first()
    }

    fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.error(LinkState.ALREADY_LINKED)
        val discordId = pendingLinks.inverse().remove(user.uniqueId) ?: return LinkResult.error(LinkState.NO_LINK_REQUEST)

        dataSource.save(Link(discordId, user.uniqueId))
        return LinkResult.success(discordId, user.uniqueId)
    }

    fun unlink(user: MinecraftUser): LinkResult {
        if (!isLinked(user)) return LinkResult.error(LinkState.NOT_LINKED)

        dataSource.delete(getQuery(user.uniqueId))
        return LinkResult(LinkState.UNLINKED, null)
    }

    private fun getQuery(minecraftId: UUID): Query<Link> {
        return dataSource.createQuery(Link::class.java).field("minecraftId").equal(minecraftId)
    }

}