package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.database.PlaytimeData
import com.nanabell.sponge.nico.module.afk.service.AfkService
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import org.spongepowered.api.entity.living.player.Player
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@RegisterService
class PlaytimeService : AbstractService<ActivityModule>() {

    private val database: DatabaseService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val afkService: AfkService? = NicoYazawa.getServiceRegistry().provideUnchecked()

    private val sessionJoins: MutableMap<UUID, Instant> = HashMap()
    private val afkDurations: MutableMap<UUID, Duration> = HashMap()

    override fun onPreEnable() {

    }

    fun getTotalPlaytime(player: Player): Duration {
        val session = getSessionPlaytime(player)

        val data = database.findById<PlaytimeData>("userId", player.uniqueId)
        return if (data != null) session.plus(data.getPlaytime()) else session
    }

    fun getSessionPlaytime(player: Player): Duration {
        var playtime = Duration.between(getOrAddNow(player), Instant.now())

        // If the user is AFK right now add the time since start.
        if (afkService?.isAfk(player) == true) {
            playtime.minus(afkService.getAfkDuration(player))
        }

        // Add any preexisting AFK times
        if (afkDurations.containsKey(player.uniqueId)) {
            playtime = playtime.minus(afkDurations[player.uniqueId])
        }

        return playtime
    }

    fun getSessionPlaytimeRaw(player: Player): Duration {
        return Duration.between(getOrAddNow(player), Instant.now())
    }

    fun addAfkDuration(player: Player, afkDuration: Duration) {
        val last = afkDurations.computeIfAbsent(player.uniqueId) { Duration.ZERO }

        afkDurations[player.uniqueId] = last.plus(afkDuration)
    }

    fun startSession(player: Player) {
        sessionJoins[player.uniqueId] = Instant.now()
    }

    fun endSession(player: Player) {
        val data = database.findById<PlaytimeData>("userId", player.uniqueId)
        if (data != null) {
            database.update("userId", player.uniqueId, database.newUpdateOperations<PlaytimeData>().set("playtime", getSessionPlaytime(player).plus(data.getPlaytime()).seconds))
        } else {
            database.save(PlaytimeData(player.uniqueId, getSessionPlaytime(player).seconds))
        }

        sessionJoins.remove(player.uniqueId)
        afkDurations.remove(player.uniqueId)
    }

    private fun getOrAddNow(player: Player): Instant {
        return sessionJoins.computeIfAbsent(player.uniqueId) { Instant.now() }
    }
}