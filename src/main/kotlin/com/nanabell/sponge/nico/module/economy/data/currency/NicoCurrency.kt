package com.nanabell.sponge.nico.module.economy.data.currency

import com.nanabell.sponge.nico.internal.extension.bold
import com.nanabell.sponge.nico.internal.extension.lightPurple
import com.nanabell.sponge.nico.internal.extension.toText
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.text.Text
import java.math.BigDecimal
import java.text.NumberFormat

class NicoCurrency : Currency {

    override fun getId(): String = "Nico-Yazawa:Nico_Points"

    override fun getName(): String = "Nico Points"

    override fun getSymbol(): Text = Text.EMPTY

    override fun getDisplayName(): Text = "Nico Point".toText()

    override fun getPluralDisplayName(): Text = "Nico Points".toText()

    override fun isDefault(): Boolean = false

    override fun getDefaultFractionDigits(): Int = 0

    override fun format(amount: BigDecimal, numFractionDigits: Int): Text {
        formatter.maximumFractionDigits = numFractionDigits

        return formatter.format(amount).lightPurple()
                .concat(symbol.bold())
                .concat(" ".toText())
                .concat(if (amount == BigDecimal.ONE) displayName else pluralDisplayName)
    }

    companion object {
        private val formatter: NumberFormat = NumberFormat.getNumberInstance()

        val instance = NicoCurrency()
    }
}