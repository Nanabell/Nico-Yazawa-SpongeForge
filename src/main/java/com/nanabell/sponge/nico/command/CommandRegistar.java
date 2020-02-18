package com.nanabell.sponge.nico.command;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.command.discordlink.LinkCommand;
import com.nanabell.sponge.nico.command.economy.NicoGetCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegistar {

    private final CommandManager commandManager;
    private final NicoYazawa plugin;

    private CommandSpec.Builder nicoCommandBuilder = CommandSpec.builder();

    public CommandRegistar(NicoYazawa plugin) {
        commandManager = Sponge.getCommandManager();
        this.plugin = plugin;

        loadCommands();
    }

    private void loadCommands() {
        // Currency
        addCommand(new NicoGetCommand());

        // Discord-Link
        addCommand(new LinkCommand());

        // Finally register the commands to Sponge
        registerCommands();
    }

    private void addCommand(SelfSpecCommand command) {
        nicoCommandBuilder.child(command.spec(), command.aliases());
    }

    private void registerCommands() {
        CommandSpec nicoCommand = nicoCommandBuilder.description(Text.of("Nico Nico Ni!"))
                .permission("nico")
                .build();

        commandManager.register(plugin, nicoCommand, "nico", "n");
    }
}
