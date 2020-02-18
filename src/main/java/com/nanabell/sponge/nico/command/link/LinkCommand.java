package com.nanabell.sponge.nico.command.link;

import com.nanabell.sponge.nico.command.SelfSpecCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class LinkCommand implements CommandExecutor, SelfSpecCommand {

    @Override
    public String[] aliases() {
        return new String[] {"link"};
    }

    @Override
    public CommandSpec spec() {
        LinkAcceptCommand acceptCommand = new LinkAcceptCommand();
        LinkDenyCommand denyCommand = new LinkDenyCommand();

        return CommandSpec.builder()
                .description(Text.of("Commands to View / Accept / Deny pending & existing Discord-Links"))
                .executor(this)
                .child(acceptCommand.spec(), acceptCommand.aliases())
                .child(denyCommand.spec(), denyCommand.aliases())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
