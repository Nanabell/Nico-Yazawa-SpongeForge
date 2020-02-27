package com.nanabell.sponge.nico.module.activity.runnable

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterRunnable
import com.nanabell.sponge.nico.internal.runnable.AbstractRunnable
import com.nanabell.sponge.nico.module.activity.ActivityModule
import com.nanabell.sponge.nico.module.activity.service.ActivityService
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@RegisterRunnable("NicoYazawa-S-ActivityReset", interval = 1, intervalUnit = TimeUnit.DAYS)
class ActivityResetRunnable : AbstractRunnable<ActivityModule>() {

    private val service: ActivityService = NicoYazawa.getServiceRegistry().provideUnchecked()

    override fun overrideDelay(): Pair<Long, TimeUnit>? {
        return calculateInitialDelay() to TimeUnit.SECONDS
    }

    override fun run() {
        service.resetRewardCounter()
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val next = now.withHour(0).withMinute(15).withSecond(0)
        if (now > next) next.plusDays(1)

        val duration = Duration.between(next, now)
        return duration.seconds
    }
}