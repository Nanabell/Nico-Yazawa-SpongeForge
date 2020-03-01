package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.database.Playtime
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import org.spongepowered.api.entity.living.player.Player
import java.time.Duration

@RegisterService
class PlaytimeService : AbstractService<ActivityModule>() {

    private val database: DatabaseService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val playTimes: MutableSet<Playtime> = HashSet()

    override fun onPreEnable() {

    }

    fun getPlayTime(player: Player): Duration {
        return get(player).getPlayTime()
    }

    fun getAfkTime(player: Player): Duration {
        return get(player).getAfkTime()
    }

    fun getActiveTime(player: Player): Duration {
        return get(player).getActiveTime()
    }

    fun getSessionPlayTime(player: Player): Duration {
        return  get(player).getSessionPlayTime()
    }

    fun getSessionAfkTime(player: Player): Duration {
        return get(player).getSessionAfkTime()
    }

    fun getSessionActiveTime(player: Player): Duration {
        return get(player).getSessionActiveTime()
    }

    fun setAfk(player: Player) {
        get(player).setAfk()
    }

    fun setActive(player: Player) {
        get(player).setActive()
    }

    fun startSession(player: Player) {
        playTimes.removeIf { it.uniqueId == player.uniqueId }
        playTimes.add(get(player))
    }

    fun endSession(player: Player) {
        val playtime = get(player)
        playtime.endSession()

        database.save(playtime)
    }

    private fun get(player: Player): Playtime {
        var playtime = playTimes.firstOrNull { it.uniqueId == player.uniqueId }
        if (playtime != null) {
            playtime.update()
            return playtime
        }

        playtime = database.findById("uniqueId", player.uniqueId)
        if (playtime == null) {
            playtime = Playtime(player.uniqueId)

            database.save(playtime)
        }

        playtime.update()
        return playtime
    }
}