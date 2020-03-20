package com.nanabell.sponge.nico.module.quest.interfaces.user

import java.util.*

interface UserStore {

    fun load(userId: UUID): IUser?
    fun loadAll(): List<IUser>

    fun save(user: IUser)
    fun saveAll(users: List<IUser>)

    fun remove(user: IUser)
    fun removeAll()

}