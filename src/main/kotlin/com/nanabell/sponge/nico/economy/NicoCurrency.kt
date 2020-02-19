package com.nanabell.sponge.nico.economy

import com.nanabell.sponge.nico.extensions.bold
import com.nanabell.sponge.nico.extensions.darkAqua
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.math.BigDecimal
import java.text.NumberFormat

class NicoCurrency : Currency {

    override fun getDisplayName(): Text {
        return "Nico Point".toText()
    }

    override fun getPluralDisplayName(): Text {
        return "Nico Points".toText()
    }

    override fun getSymbol(): Text {
        return "￥".toText()
    }

    override fun format(amount: BigDecimal, numFractionDigits: Int): Text {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = numFractionDigits
        formatter.maximumFractionDigits = numFractionDigits

        return formatter.format(amount).darkAqua()
                .concat(symbol.bold())
                .concat(" ".toText())
                .concat(if (amount > BigDecimal.ZERO) pluralDisplayName else displayName)
    }

    override fun getDefaultFractionDigits(): Int {
        return 0
    }

    override fun isDefault(): Boolean {
        return true
    }

    override fun getId(): String {
        return "nico-points"
    }

    override fun getName(): String {
        return "Nico Points"
    }

    companion object {
        val currency = NicoCurrency()
    }
}