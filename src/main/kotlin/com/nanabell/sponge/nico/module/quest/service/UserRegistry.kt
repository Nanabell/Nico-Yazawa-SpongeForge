package com.nanabell.sponge.nico.module.quest.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.user.User
import com.nanabell.sponge.nico.module.quest.interfaces.user.IUser
import com.nanabell.sponge.nico.module.quest.interfaces.user.UserStore
import com.nanabell.sponge.nico.module.quest.store.ConfigUserStore
import org.spongepowered.api.Sponge
import java.util.*

@RegisterService
class UserRegistry : AbstractService<QuestModule>() {

    private val cache = Caffeine.newBuilder().build<UUID, IUser> { store.load(it) }
    private lateinit var store: UserStore

    override fun onPreEnable() {
        val path = Sponge.getConfigManager().getPluginConfig(plugin).directory.resolve("users.conf")

        store = ConfigUserStore(path)
        store.loadAll().forEach { cache.put(it.id, it) }
    }

    fun has(user: IUser) = has(user.id)
    fun has(userId: UUID) = cache[userId] != null
    fun get(userId: UUID) = cache[userId] ?: addNew(userId)
    fun getAll() = cache.asMap().values
    fun set(user: IUser) = store.save(user).also { cache.put(user.id, user) }
    fun remove(user: IUser) = store.remove(user).also { cache.invalidate(user.id) }

    private fun addNew(userId: UUID): IUser {
        val user = User(userId)

        set(user)
        return user
    }

}
