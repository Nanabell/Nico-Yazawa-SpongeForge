package com.nanabell.sponge.nico.internal.annotation

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterRunnable(

        /**
         * Unique Task ID
         */
        val value: String,

        /**
         *  Task Interval, interval <= 0 to never repeat
         */
        val interval: Long = -1,

        /**
         * Interval TimeUnit
         */
        val intervalUnit: TimeUnit = TimeUnit.SECONDS,

        /**
         *  Initial delay, delay <= 0 for no delay
         */
        val delay: Long = 5,

        /**
         * Delay TimeUnit
         */
        val delayUnit: TimeUnit = TimeUnit.SECONDS,

        /**
         * Run the Task Async?
         */
        val isAsync: Boolean = false
)