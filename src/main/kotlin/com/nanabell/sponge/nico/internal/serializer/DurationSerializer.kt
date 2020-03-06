package com.nanabell.sponge.nico.internal.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.time.Duration
import java.time.temporal.ChronoUnit

@Suppress("UnstableApiUsage")
class DurationSerializer : TypeSerializer<Duration> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Duration? {
        return Duration.of(value.long, ChronoUnit.SECONDS)
    }

    override fun serialize(type: TypeToken<*>, obj: Duration?, value: ConfigurationNode) {
        value.value = obj?.seconds
    }
}