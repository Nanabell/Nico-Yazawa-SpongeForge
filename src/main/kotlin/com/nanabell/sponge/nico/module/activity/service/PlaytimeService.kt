package com.nanabell.sponge.nico.module.activity.service

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.database.Playtime
import com.nanabell.sponge.nico.module.core.service.DatabaseService
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import java.time.Duration

@RegisterService
class PlaytimeService : AbstractService<ActivityModule>() {

    private val database: DatabaseService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val playTimes: MutableSet<Playtime> = HashSet()

    override fun onPreEnable() {

    }

    fun getPlayTime(user: User): Duration {
        return get(user).getPlayTime()
    }

    fun getAfkTime(user: User): Duration {
        return get(user).getAfkTime()
    }

    fun getActiveTime(user: User): Duration {
        return get(user).getActiveTime()
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

    private fun get(user: User): Playtime {
        var playtime = playTimes.firstOrNull { it.uniqueId == user.uniqueId }
        if (playtime != null) {
            return playtime.also { it.update() }
        }

        playtime = database.findById("uniqueId", user.uniqueId)
        if (playtime == null) {
            playtime = Playtime(user.uniqueId)

            database.save(playtime)
        }

        playtime.update()
        return playtime
    }
}