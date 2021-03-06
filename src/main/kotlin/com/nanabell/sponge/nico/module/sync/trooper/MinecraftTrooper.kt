package com.nanabell.sponge.nico.module.sync.trooper

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import com.nanabell.sponge.nico.module.sync.config.SyncConfig
import com.nanabell.sponge.nico.module.sync.data.Troop
import com.nanabell.sponge.nico.module.sync.interfaces.ITrooper
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.util.Tristate

class MinecraftTrooper(private val config: SyncConfig) : ITrooper {

    private val logger = NicoYazawa.getPlugin().getLogger(javaClass.simpleName)

    private val serviceAvailable = Sponge.getServiceManager().isRegistered(PermissionService::class.java)
    private val discordService: DiscordService? = NicoYazawa.getServiceRegistry().provide()

    override fun exists(player: MinecraftUser): Boolean {
        return serviceAvailable
    }

    override fun hasTroop(player: MinecraftUser, troop: String): Boolean {
        return player.hasPermission(troop)
    }

    override fun getTroops(player: MinecraftUser): List<Troop> {
        val result = mutableListOf<Troop>()
        config.getMinecraftTroops().forEach {
            if (hasTroop(player, it.permission))
                result.add(it)
        }

        return result
    }

    override fun addTroop(player: MinecraftUser, troop: Troop) {
        if (!player.hasPermission(troop.permission)) {
            player.subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, troop.permission, Tristate.TRUE).thenApply {

                if (it) {
                    player.player.orNull()?.sendMessage("+ Added Permission '${troop.permission}' because you have the Discord role ${getRoleName(troop.role)}".toText().green())
                    logger.info("Added Permission {} to Player {}. {}", troop.permission, player.name, troop)

                } else {
                    logger.warn("Failed to Add Permission {} to Player {}, {}", troop.permission, player.name, troop)
                }
            }
        }
    }

    override fun removeTroop(player: MinecraftUser, troop: Troop) {
        if (player.hasPermission(troop.permission)) {
            player.subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, troop.permission, Tristate.UNDEFINED).thenApply {

                if (it) {
                    player.player.orNull()?.sendMessage("- Removed Permission '${troop.permission}' because you do not have the required Discord role ${getRoleName(troop.role)}".toText().red())
                    logger.info("Removed Permission {} from Player {}. {}", troop.permission, player.name, troop)

                } else {
                    logger.warn("Failed to Remove Permission {} from Player {}, {}", troop.permission, player.name, troop)
                }
            }
        }
    }

    private fun getRoleName(longId: Long): String {
        return discordService?.getRole(longId)?.name ?: "Unable to retrieve role name!"
    }

    override fun toString(): String {
        return javaClass.simpleName
    }
}