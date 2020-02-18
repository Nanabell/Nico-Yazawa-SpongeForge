package com.nanabell.sponge.nico.command.link;

import com.nanabell.sponge.nico.command.SelfSpecCommand;
import com.nanabell.sponge.nico.link.LinkResult;
import com.nanabell.sponge.nico.link.LinkService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class LinkAcceptCommand implements CommandExecutor, SelfSpecCommand {

    private LinkService linkService = Sponge.getServiceManager().provideUnchecked(LinkService.class);

    @Override
    public String[] aliases() {
        return new String[]{"accept", "a"};
    }

    @Override
    public CommandSpec spec() {
        return CommandSpec.builder()
                .description(Text.of("Accept a pending Discord-Link Request"))
                .arguments(GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                GenericArguments.player(Text.of("target")), "nico.link.accept.others")))
                .executor(this)
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!args.hasAny(Text.of("target")) && !(src instanceof Player)) {
            throw new CommandException(Text.of("Cannot Target " + src.getName() + ". Valid Target is [Player]"));
        }

        Player target = src instanceof Player ? (Player) src : args.requireOne("target");

        if (!linkService.pendingLink(target.getUniqueId())) {
            if (linkService.isLinked(target.getUniqueId())) {
                // Already Linked
            } else {
                // No Pending Link
            }
        }

        LinkResult result = linkService.confirmLink(target.getUniqueId());
        if (result != LinkResult.SUCCESS) {

        }

        return CommandResult.success();
    }
}
