package com.nanabell.sponge.nico.module.core.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.CoreModule
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@RegisterService
class PlaytimeService : AbstractService<CoreModule>() {

    private val joinTimes: MutableMap<UUID, Instant> = HashMap()

    override fun onPreEnable() {

    }

    fun add(userId: UUID, joinTime: Instant) {
        joinTimes[userId] = joinTime
    }

    fun get(userId: UUID): Instant? {
        return joinTimes[userId]
    }

    fun computeIfAbsent(userId: UUID, function: (UUID) -> Instant): Instant {
        return joinTimes.computeIfAbsent(userId, function)
    }

    fun remove(userId: UUID) {
        joinTimes.remove(userId)
    }
}