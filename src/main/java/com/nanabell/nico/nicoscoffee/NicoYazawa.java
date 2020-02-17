package com.nanabell.nico.nicoscoffee;

import com.google.inject.Inject;
import com.nanabell.nico.nicoscoffee.activity.ActivityTracker;
import com.nanabell.nico.nicoscoffee.command.NicoGetCommand;
import com.nanabell.nico.nicoscoffee.command.NicoSetCommand;
import com.nanabell.nico.nicoscoffee.config.Config;
import com.nanabell.nico.nicoscoffee.config.MainConfig;
import com.nanabell.nico.nicoscoffee.economy.NicoAccount;
import com.nanabell.nico.nicoscoffee.economy.NicoEconomyService;
import com.nanabell.nico.nicoscoffee.storage.Persistable;
import com.nanabell.nico.nicoscoffee.storage.PersistenceManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;

@Plugin(
        id = "nico-Yazawa",
        name = "Nico Yazawa",
        description = "Linking Minecraft & Discord Nico Style!",
        authors = {
                "Nanabell"
        }
)
public class NicoYazawa {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private Config<MainConfig> configManager;

    @Listener
    public void onInit(GameInitializationEvent event) {
        configManager = new Config<>(MainConfig.class, "nicos-yazawa.conf", configDir);

        PersistenceManager persistenceManager = new PersistenceManager();
        persistenceManager.register(new Persistable<>(configManager.get().getDatabaseUrl(), NicoAccount.class));

        Sponge.getServiceManager().setProvider(this, PersistenceManager.class, persistenceManager);
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new NicoEconomyService());

        CommandManager manager = Sponge.getCommandManager();
        CommandSpec nicoGet = CommandSpec.builder()
                .description(Text.of("View your current Nico Points"))
                .executor(new NicoGetCommand())
                .build();

        CommandSpec nicoSet = CommandSpec.builder()
                .description(Text.of("Set your Balance to a specified amount"))
                .arguments(GenericArguments.bigDecimal(Text.of("amount")))
                .executor(new NicoSetCommand())
                .build();

        manager.register(this, nicoGet, "nico-get");
        manager.register(this, nicoSet, "nico-set");
    }

    @Listener
    public void onGameAboutToStartServer(GameAboutToStartServerEvent event) {
        ActivityTracker activityTracker = new ActivityTracker(this);
        activityTracker.init();

        Sponge.getServiceManager().setProvider(this, ActivityTracker.class, activityTracker);
    }

    @Listener
    public void onGameReload(GameReloadEvent event) {
        configManager.reload();
    }

    public Logger getLogger() {
        return logger;
    }

    public Config<MainConfig> getConfigManager() {
        return configManager;
    }

    public static PersistenceManager getPersistenceManager() {
        return Sponge.getServiceManager().provideUnchecked(PersistenceManager.class);
    }
}
