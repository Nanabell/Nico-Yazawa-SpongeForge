package com.nanabell.sponge.nico.command;

import com.nanabell.sponge.nico.economy.NicoCurrency;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class NicoGetCommand implements CommandExecutor {

    private final ServiceManager serviceManager = Sponge.getServiceManager();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("This Command can only be used by players!"));
        }

        Player player = (Player) src;

        Optional<EconomyService> oService = serviceManager.provide(EconomyService.class);
        if (!oService.isPresent()) {
            throw new CommandException(Text.of("No EconomyService registered!"));
        }

        EconomyService service = oService.get();
        NicoCurrency currency = NicoCurrency.getCurrency();
        UniqueAccount account = service.getOrCreateAccount(player.getUniqueId()).orElseThrow(() -> new CommandException(Text.of("Unable to create User Account for User" + player)));

        player.sendMessage(Text.of("You currently have ").concat(currency.format(account.getBalance(currency))));
        return CommandResult.success();
    }
}
