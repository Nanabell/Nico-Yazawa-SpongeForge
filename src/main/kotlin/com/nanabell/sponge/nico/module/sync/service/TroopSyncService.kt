package com.nanabell.sponge.nico.module.sync.service

import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.extension.MinecraftUser
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.sync.SyncModule
import com.nanabell.sponge.nico.module.sync.config.SyncConfig
import com.nanabell.sponge.nico.module.sync.interfaces.ITrooper
import com.nanabell.sponge.nico.module.sync.misc.TroopSource
import com.nanabell.sponge.nico.module.sync.trooper.DiscordTrooper
import com.nanabell.sponge.nico.module.sync.trooper.MinecraftTrooper

@RegisterService
class TroopSyncService : AbstractService<SyncModule>() {

    private lateinit var config: SyncConfig

    private lateinit var minecraft: ITrooper
    private lateinit var discord: ITrooper

    override fun onPreEnable() {
        this.config = module.getConfigOrDefault()

        this.minecraft = MinecraftTrooper(config)
        this.discord = DiscordTrooper(config)
    }

    fun sync(user: MinecraftUser, sourceTroop: TroopSource) {
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

                        } else {
                            logger.trace("User {} already has target troop {}", user.name, it)
                        }

                    } else {
                        logger.trace("User {} does meet the source {} requirement", user.name, it)

                        // Does he still have it?
                        if (target.hasTroop(user, troop)) {
                            logger.trace("User {} still has target troop {}. Removing...", user.name, it)
                            target.removeTroop(user, it) // Remove it

                        } else {
                            logger.trace("User {} does not have target troop {}", user.name, it)
                        }
                    }
                }
            }
        }

        logger.debug("Finished Syncing {} for player {}", sourceTroop, user.name)
    }

}