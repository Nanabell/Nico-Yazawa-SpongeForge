package com.nanabell.sponge.nico.activity;

import com.nanabell.sponge.nico.NicoYazawa;
import com.nanabell.sponge.nico.config.ActivityConfig;
import com.nanabell.sponge.nico.config.Config;
import com.nanabell.sponge.nico.config.MainConfig;
import com.nanabell.sponge.nico.config.PaymentConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class ActivityTracker implements Runnable {

    private static final Random RANDOM = new Random();

    private final NicoYazawa plugin;
    private final Config<MainConfig> configManager;
    private final ServiceManager serviceManager;
    private final Logger logger;

    private Task task;
    private int dayOfYear;

    private Map<UUID, Integer> activityMap = new HashMap<>();
    private Map<UUID, Integer> paymentMap = new HashMap<>();


    public ActivityTracker(NicoYazawa plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.serviceManager = Sponge.getServiceManager();
        this.logger = plugin.getLogger();
    }

    public void init() {
        if (task != null) {
            logger.warn("Activity Task is running but init was called. Cancelling old Task");
            cancel();
        }

        task = Sponge.getScheduler().createTaskBuilder()
                .execute(this)
                .delayTicks(20)
                .intervalTicks(20)
                .name("Nicos-Coffe_Activity_Tracker")
                .async()
                .submit(plugin);

        dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        logger.info("Started Activity Tracker Task");
    }

    public void cancel() {
        try {
            task.cancel();
        } finally {
            task = null;
        }
    }

    @Override
    public void run() {
        ActivityConfig activityConfig = configManager.get().getActivityConfig();

        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (activityConfig.getDisabledWorlds().contains(player.getWorld().getName())) {
                continue;
            }

            activityMap.put(player.getUniqueId(), activityMap.computeIfAbsent(player.getUniqueId(), uuid -> 0) + 1);
            if (activityMap.get(player.getUniqueId()) >= activityConfig.getPaymentInterval()) {
                activityMap.remove(player.getUniqueId());
                payout(player, activityConfig);
            }
        }

        int tmp = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (dayOfYear != tmp) {
            logger.info("New Day. Resetting daily payment Limits & activity Maps");
            activityMap.clear();
            paymentMap.clear();

            dayOfYear = tmp;
        }
    }

    private void payout(Player player, ActivityConfig activityConfig) {
        for (PaymentConfig paymentConfig : activityConfig.getPaymentConfigs()) {
            if (paymentConfig.getDailyPaymentLimit() != 0 && paymentMap.getOrDefault(player.getUniqueId(), 0) > paymentConfig.getDailyPaymentLimit()) {
                continue;
            }

            if (paymentConfig.getPaymentChance() == 0 || (paymentConfig.getPaymentChance() != 100 && RANDOM.nextInt(101) > paymentConfig.getPaymentChance())) {
                continue;
            }

            if (!player.hasPermission(paymentConfig.getRequiredPermission())) {
                continue;
            }

            paymentMap.put(player.getUniqueId(), paymentMap.computeIfAbsent(player.getUniqueId(), uuid -> paymentConfig.getPaymentAmount()));

            EconomyService economyService = serviceManager.provideUnchecked(EconomyService.class);
            Optional<UniqueAccount> oAccount = economyService.getOrCreateAccount(player.getUniqueId());
            if (oAccount.isPresent()) {
                UniqueAccount account = oAccount.get();

                account.deposit(economyService.getDefaultCurrency(), new BigDecimal(paymentConfig.getPaymentAmount()), Cause.of(EventContext.empty(), this));
                logger.debug("Deposited " + paymentConfig.getPaymentAmount() + " Currency to " + player + "'s Account");

                player.sendMessage(ChatTypes.ACTION_BAR, Text.of("You have earned ")
                        .concat(Text.of(economyService.getDefaultCurrency().format(new BigDecimal(paymentConfig.getPaymentAmount()))))
                        .concat(Text.of(" for playing "))
                        .concat(Text.of(Duration.ofSeconds(activityConfig.getPaymentInterval()).toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase()))
                        .concat(Text.of(" Online!")));
            }

            return;
        }
    }
}
