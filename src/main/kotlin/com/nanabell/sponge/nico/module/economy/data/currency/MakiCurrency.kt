package com.nanabell.sponge.nico.module.economy.data.currency

import com.nanabell.sponge.nico.extensions.bold
import com.nanabell.sponge.nico.extensions.red
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.text.Text
import java.math.BigDecimal
import java.text.NumberFormat

class MakiCurrency : Currency {

    override fun getId(): String = "Nico-Yazawa:Maki_Points"

    override fun getName(): String = "Maki Points"

    override fun getSymbol(): Text = Text.EMPTY

    override fun getDisplayName(): Text = "Maki Point".toText()

    override fun getPluralDisplayName(): Text = "Maki Points".toText()

    override fun isDefault(): Boolean = true

    override fun getDefaultFractionDigits(): Int = 0

    override fun format(amount: BigDecimal, numFractionDigits: Int): Text {
        formatter.maximumFractionDigits = numFractionDigits

        return formatter.format(amount).red()
                .concat(symbol.bold())
                .concat(" ".toText())
                .concat(if (amount == BigDecimal.ONE) pluralDisplayName else displayName)
    }

    companion object {
        private val formatter: NumberFormat = NumberFormat.getNumberInstance()

        val instance = MakiCurrency()
    }
}
