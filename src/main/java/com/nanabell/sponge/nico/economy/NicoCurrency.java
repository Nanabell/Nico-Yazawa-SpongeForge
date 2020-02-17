package com.nanabell.sponge.nico.economy;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NicoCurrency implements Currency {

    private static final NicoCurrency INSTNACE = new NicoCurrency();

    public static NicoCurrency getCurrency() {
        return INSTNACE;
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Nico Point");
    }

    @Override
    public Text getPluralDisplayName() {
        return Text.of("Nico Points");
    }

    @Override
    public Text getSymbol() {
        return Text.of("￥");
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(numFractionDigits);
        formatter.setMaximumFractionDigits(numFractionDigits);

        return Text.of(TextColors.DARK_AQUA, formatter.format(amount)).concat(
                Text.builder().style(TextStyles.BOLD).append(getSymbol()).style(TextStyles.RESET)
                .append(Text.of(" "))
                .append(Text.of(amount.compareTo(BigDecimal.ONE) > 0 ? getPluralDisplayName() : getDisplayName())).build());
    }

    @Override
    public int getDefaultFractionDigits() {
        return 0;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getId() {
        return "nico-points";
    }

    @Override
    public String getName() {
        return "Nico Points";
    }
}
