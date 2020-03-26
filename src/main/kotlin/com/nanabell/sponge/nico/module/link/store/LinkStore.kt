package com.nanabell.sponge.nico.module.link.store

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import com.nanabell.sponge.nico.module.link.database.Link
import java.util.*

class LinkStore {

    private val databaseService: DatabaseService = NicoYazawa.getServiceRegistry().provideUnchecked()

    fun load(minecraftId: UUID): Link? {
        return databaseService.findById("minecraftId", minecraftId)
    }

    fun load(discordId: Long): Link? {
        return databaseService.findById("discordId", discordId)
    }

    fun loadAll(): List<Link> {
        return databaseService.dataStore.find(Link::class.java).find().toList()
    }

    fun save(link: Link) {
        databaseService.save(link)
    }

    fun remove(link: Link) {
        databaseService.dataStore.delete(link)
    }

    fun removeAll() {
        loadAll().forEach { remove(it) }
    }
}