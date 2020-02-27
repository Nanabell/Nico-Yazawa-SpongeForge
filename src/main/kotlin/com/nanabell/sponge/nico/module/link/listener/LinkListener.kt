package com.nanabell.sponge.nico.module.link.listener

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.RegisterListener
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.internal.listener.AbstractListener
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import com.nanabell.sponge.nico.module.link.LinkModule
import com.nanabell.sponge.nico.module.link.config.LinkConfig
import com.nanabell.sponge.nico.module.link.event.LinkedEvent
import com.nanabell.sponge.nico.module.link.event.UnlinkedEvent
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.util.Tristate

@RegisterListener
class LinkListener : AbstractListener<LinkModule>() {

    private val discordService: DiscordService? = NicoYazawa.getServiceRegistry().provide()

    private lateinit var config: LinkConfig

    override fun onReady() {
        config = module.getConfigOrDefault()
    }

    @Listener
    fun onLinked(event: LinkedEvent) {
        val player = event.minecraftUser.player.orNull()

        addPermission(event.minecraftUser, player)
        addRole(event.discordUser, player)
    }

    @Listener
    fun unUnlinked(event: UnlinkedEvent) {
        val minecraftUser = event.uniqueId.toMinecraftUser()
        if (minecraftUser != null) {
            val player = minecraftUser.player.orNull()

            removePermission(minecraftUser, player)

            val discordUser = event.userId.toDiscordUser()
            if (discordUser != null) {
                removeRole(discordUser, player)
            }
        }
    }

    private fun addPermission(mUser: MinecraftUser, player: Player?) {
        if (checkPermissionRequirements(mUser, false)) return

        val result = mUser.subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, config.linkPermission, Tristate.TRUE).get()
        if (result) {
            player?.sendMessage("You've been given the Permission".toText().gold().concat(config.linkPermission.toText().yellow()))
            logger.info("Applied linking-permission ${config.linkPermission} to User ${mUser.name}")

        } else {
            player?.sendMessage("Unable to assign Permission ${config.linkPermission}! Contact an Administrator".toText().darkRed())
            logger.error("Unable to assign Permission ${config.linkPermission} to User ${mUser.name}")
        }
    }

    private fun removePermission(mUser: MinecraftUser, player: Player?) {
        if (checkPermissionRequirements(mUser, true)) return

        val result = mUser.subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, config.linkPermission, Tristate.UNDEFINED).get()
        if (result) {
            player?.sendMessage("You've lost the Permission ".toText().gold().concat(config.linkPermission.toText().yellow()))
            logger.info("Removed linking-permission ${config.linkPermission} from User ${mUser.name}")
        } else {
            player?.sendMessage("Unable to remove Group ${config.linkPermission}! Contact an Administrator".toText().darkRed())
            logger.error("Unable to remove Group ${config.linkPermission} to User ${mUser.name}")
        }
    }

    private fun addRole(dUser: DiscordUser, player: Player?) {
        val (member, role) = checkRoleRequirements(dUser, false) ?: return

        if (discordService?.addRole(member, role) == true) {
            player?.sendMessage("You've been given the Discord Role ".toText().gold()
                    .concat(role.name.toText().yellow())
                    .concat(" in the Guild ".toText().gold())
                    .concat(member.guild.name.toText().yellow()))
        } else {
            player?.sendMessage("Unable to apply Discord Role ${role.name} in Guild ${member.guild.name}, Contact an Administrator!".toText().darkRed())
        }
    }

    private fun removeRole(dUser: DiscordUser, player: Player?) {
        val (member, role) = checkRoleRequirements(dUser, true) ?: return

        if (discordService?.removeRole(member, role) == true) {
            player?.sendMessage("You've lost the Discord Role ".toText().gold()
                    .concat(role.name.toText().yellow())
                    .concat(" in the Guild ".toText().gold())
                    .concat(member.guild.name.toText().yellow()))
        } else {
            player?.sendMessage("Unable to remove Discord Role ${role.name} in Guild ${member.guild.name}, Contact an Administrator!".toText().darkRed())
        }
    }

    private fun checkPermissionRequirements(mUser: MinecraftUser, isRemove: Boolean): Boolean {
        if (config.linkPermission.isEmpty()) {
            logger.debug("Unable to pass check for linking-group. No link-group specified in Configuration")
            return false
        }

        val hasPermission = mUser.subjectData.getPermissions(SubjectData.GLOBAL_CONTEXT)[config.linkPermission] == true
        if (isRemove) {
            if (!hasPermission) {
                logger.warn("Unable to pass check for linking-group. User ${mUser.name} does not have ${config.linkPermission}")
                return false
            }
        } else {
            if (hasPermission) {
                logger.warn("Unable to pass check for linking-group. User ${mUser.name} already has ${config.linkPermission}")
                return false
            }
        }

        return true
    }

    private fun checkRoleRequirements(dUser: DiscordUser, isRemove: Boolean): Pair<Member, Role>? {
        if (config.linkRole == -1L) {
            logger.debug("Unable to pass check for Discord Role. No Role configured in config")
            return null
        }

        val role = discordService?.getRole(config.linkRole)
        if (role == null) {
            logger.warn("Unable to pass check for Discord Role. Configured Role ${config.linkRole} not Found!")
            return null
        }

        val member = discordService?.getMember(dUser.idLong)
        if (member == null) {
            logger.warn("Unable to pass check for Discord Role. Member ${dUser.asTag} no longer part of Guild")
            return null
        }

        val hasRole = member.roles.contains(role)
        if (isRemove) {
            if (!hasRole) {
                logger.warn("Unable to pass check for Discord Role. Member ${dUser.asTag} does not have the role ${role.name}")
                return null
            }
        } else {
            if (hasRole) {
                logger.warn("Unable to pass check for Discord Role. Member ${dUser.asTag} already has the role ${role.name}")
                return null
            }
        }


        return Pair(member, role)
    }
}