package com.nanabell.sponge.nico.link.sync

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.MinecraftUser
import org.spongepowered.api.Sponge
import java.util.concurrent.TimeUnit

class TroopSyncService : Runnable {

    private val logger = NicoYazawa.getPlugin().getLogger(javaClass.simpleName)

    private val minecraft = MinecraftTrooper()
    private val discord = DiscordTrooper()

    private val config get() = NicoYazawa.getPlugin().getConfig().get().discordLinkConfig.syncConfig

    fun init() {
        if (!config.discordSync && !config.minecraftSync) {
            logger.info("Neither Syncing Discord -> Minecraft or Minecraft -> Discord. Disabling TroopSyncService.")
            return
        }

        Sponge.getScheduler().createTaskBuilder()
                .name("NicoYazawa-A-SyncService")
                .async()
                .delay(5, TimeUnit.MINUTES)
                .interval(5, TimeUnit.MINUTES)
                .execute(this)
                .submit(NicoYazawa.getPlugin())

        logger.info("Enabled TroopSyncService. DiscordSync: {}, MinecraftSync: {}", config.discordSync, config.minecraftSync)
    }

    override fun run() {
        logger.debug("Starting Sync run")
        for (player in Sponge.getServer().onlinePlayers) {

            if (config.discordSync)
                sync(player, TroopSource.DISCORD)

            if (config.minecraftSync)
                sync(player, TroopSource.MINECRAFT)

        }
        logger.debug("Finished Sync run")
    }

    private fun sync(user: MinecraftUser, sourceTroop: TroopSource) {
        logger.debug("Started Syncing {} for player {}", sourceTroop, user.name)

        val source = if (sourceTroop == TroopSource.MINECRAFT) minecraft else discord
        val target = if (sourceTroop == TroopSource.MINECRAFT) discord else minecraft
        val targetTroop = sourceTroop.other()

        // Does the user exist in the source System?
        if (source.exists(user)) {
            logger.trace("User {} exists in source Trooper {}", user.name, source)

            // Get the users Source Troops and map them to the Target variant.
            // This is a list of Troops the user wants to have on the other side
            val destinationTroops = source.getTroops(user).map { it.getFromSource(targetTroop) }
            logger.trace("User {} Target Troops: {}", user.name, destinationTroops)

            // Does our user exist in he Target System?
            if (target.exists(user)) {
                logger.trace("User {} exists in target Trooper {}", user.name, target)

                // Get all Troops for the Source and iterate over the Targets for that
                config.getTroopsFrom(sourceTroop).forEach {
                    val troop = it.getFromSource(targetTroop)

                    logger.trace("User {} running troop check for {}", user.name, it)

                    // Does the user want to have this Troop?
                    if (destinationTroops.contains(troop)) {
                        logger.trace("User {} has source troop {}, eligible for target", user.name, it)

                        // Does he not already have it?
                        if (!target.hasTroop(user, troop)) {
                            logger.trace("User {} does not yet have target troop {}. Adding...", user.name, it)
                            target.addTroop(user, it) // Add it

                        } else  {
                            logger.trace("User {} already has target troop {}", user.name, it)
                        }

                    } else {
                        logger.trace("User {} does meet the source {} requirement", user.name, it)

                        // Does he still have it?
                        if (target.hasTroop(user, troop)) {
                            logger.trace("User {} still has target troop {}. Removing...", user.name, it)
                            target.removeTroop(user, it) // Remove it

                        } else  {
                            logger.trace("User {} does not have target troop {}", user.name, it)
                        }
                    }
                }
            }
        }

        logger.debug("Finished Syncing {} for player {}", sourceTroop, user.name)
    }
}