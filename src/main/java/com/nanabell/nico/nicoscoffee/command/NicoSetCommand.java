package com.nanabell.nico.nicoscoffee.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

public class NicoSetCommand implements CommandExecutor {

    private ServiceManager serviceManager = Sponge.getServiceManager();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("This Command can only be used by players!"));
        }

        if (!args.getOne("amount").isPresent()) {
            throw new CommandException(Text.of("You must specify the amount that the account will be set to!"));
        }

        Player player = (Player) src;
        EconomyService service = serviceManager.provideUnchecked(EconomyService.class);

        Currency currency = service.getDefaultCurrency();
        UniqueAccount account = service.getOrCreateAccount(player.getUniqueId()).orElseThrow(() -> new CommandException(Text.of("Unable to created Economy Account!")));
        TransactionResult result = account.setBalance(currency, args.requireOne("amount"), Cause.of(EventContext.empty(), this));

        if (result.getResult() == ResultType.SUCCESS) {
            player.sendMessage(Text.of("Your Balance has been set to ")
                    .concat(currency.format(result.getAmount())));
        } else {
            player.sendMessage(Text.of("Unable to set Balance to ")
                    .concat(currency.format(result.getAmount()))
                    .concat(Text.NEW_LINE)
                    .concat(Text.of("Reason: "))
                    .concat(Text.of(result.getType().getName())));
        }

        return CommandResult.success();
    }
}
