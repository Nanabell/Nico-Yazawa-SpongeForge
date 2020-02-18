package com.nanabell.sponge.nico;

import com.google.inject.Inject;
import com.nanabell.sponge.nico.activity.ActivityTracker;
import com.nanabell.sponge.nico.command.CommandRegistar;
import com.nanabell.sponge.nico.config.Config;
import com.nanabell.sponge.nico.config.MainConfig;
import com.nanabell.sponge.nico.economy.NicoAccount;
import com.nanabell.sponge.nico.economy.NicoEconomyService;
import com.nanabell.sponge.nico.link.LinkService;
import com.nanabell.sponge.nico.link.MemoryLinkService;
import com.nanabell.sponge.nico.storage.Persistable;
import com.nanabell.sponge.nico.storage.PersistenceManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.economy.EconomyService;

import java.nio.file.Path;

@Plugin(
        id = "nico-yazawa",
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
        ServiceManager serviceManager = Sponge.getServiceManager();

        PersistenceManager persistenceManager = new PersistenceManager();
        persistenceManager.register(new Persistable<>(configManager.get().getDatabaseUrl(), NicoAccount.class));

        serviceManager.setProvider(this, PersistenceManager.class, persistenceManager);
        serviceManager.setProvider(this, EconomyService.class, new NicoEconomyService());
        serviceManager.setProvider(this, LinkService.class, new MemoryLinkService(this));
        serviceManager.setProvider(this, CommandRegistar.class, new CommandRegistar(this));

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

    public ServiceManager getServiceManager() {
        return Sponge.getServiceManager();
    }

    public static PersistenceManager getPersistenceManager() {
        return Sponge.getServiceManager().provideUnchecked(PersistenceManager.class);
    }
}
