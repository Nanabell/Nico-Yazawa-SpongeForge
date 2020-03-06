package com.nanabell.sponge.nico.internal.serializer

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.module.economy.data.currency.MakiCurrency
import com.nanabell.sponge.nico.module.economy.data.currency.NicoCurrency
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.service.economy.Currency

@Suppress("UnstableApiUsage")
class CurrencySerializer : TypeSerializer<Currency> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Currency? {
        val className = value.string
        if (className != null) {
            return when (className) {
                "MakiCurrency" -> MakiCurrency.instance
                "NicoCurrency" -> NicoCurrency.instance
                else -> null
            }
        }

        return null
    }

    override fun serialize(type: TypeToken<*>, obj: Currency?, value: ConfigurationNode) {
        if (obj != null)
            value.value = obj.javaClass.simpleName
    }
}