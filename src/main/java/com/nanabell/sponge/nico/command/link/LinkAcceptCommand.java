package com.nanabell.sponge.nico.command.link;

import com.nanabell.sponge.nico.command.SelfSpecCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class LinkAcceptCommand implements CommandExecutor, SelfSpecCommand {

    @Override
    public String[] aliases() {
        return new String[]{"accept", "a"};
    }

    @Override
    public CommandSpec spec() {
        return CommandSpec.builder()
                .description(Text.of("Accept a pending Discord-Link Request"))
                .executor(this)
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
