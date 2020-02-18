package com.nanabell.sponge.nico.command;

import org.spongepowered.api.command.spec.CommandSpec;

public interface SelfSpecCommand {

    String[] aliases();

    CommandSpec spec();
}
