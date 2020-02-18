package com.nanabell.sponge.nico.command.economy;

import com.nanabell.sponge.nico.command.SelfSpecCommand;
import com.nanabell.sponge.nico.economy.NicoCurrency;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class NicoGetCommand implements CommandExecutor, SelfSpecCommand {

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

    @Override
    public @NotNull String[] aliases() {
        return new String[]{"get"};
    }

    @Override
    public @NotNull CommandSpec spec() {
        return CommandSpec.builder()
                .description(Text.of("View your current Nico Points"))
                .executor(this)
                .build();
    }
}
