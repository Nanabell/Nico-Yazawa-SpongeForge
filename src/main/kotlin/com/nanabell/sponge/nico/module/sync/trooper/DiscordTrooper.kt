package com.nanabell.sponge.nico.module.sync.trooper

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.*
import com.nanabell.sponge.nico.module.discord.service.DiscordService
import com.nanabell.sponge.nico.module.link.service.LinkService
import com.nanabell.sponge.nico.module.sync.config.SyncConfig
import com.nanabell.sponge.nico.module.sync.data.Troop
import com.nanabell.sponge.nico.module.sync.interfaces.ITrooper
import net.dv8tion.jda.api.entities.Member

class DiscordTrooper(private val config: SyncConfig) : ITrooper {

    private val discordService = NicoYazawa.getServiceRegistry().provideUnchecked(DiscordService::class)
    private val linkService = NicoYazawa.getServiceRegistry().provideUnchecked(LinkService::class)

    override fun exists(player: MinecraftUser): Boolean {
        return getMember(player) != null
    }

    override fun hasTroop(player: MinecraftUser, troop: String): Boolean {
        val member = getMember(player) ?: return false
        for (role in member.roles) {
            if (role.id == troop)
                return true
        }

        return false
    }

    override fun getTroops(player: MinecraftUser): List<Troop> {
        val member = getMember(player) ?: return emptyList()
        val roleIds = member.roles.map { it.idLong }

        val result = mutableListOf<Troop>()
        config.getDiscordTroops().forEach {
            if (roleIds.contains(it.role))
                result.add(it)
        }

        return result
    }

    override fun addTroop(player: MinecraftUser, troop: Troop) {
        val member = getMember(player) ?: return
        val role = discordService.getRole(troop.role) ?: return

        if (!member.roles.contains(role)) {
            if (discordService.addRole(member, role)) {
                player.player.orNull()?.sendMessage("+ Added Role ${role.name} to your Discord Account ${member.user.asTag} because you have the permission '${troop.permission}'".toText().green())
            }
        }
    }

    override fun removeTroop(player: MinecraftUser, troop: Troop) {
        val member = getMember(player) ?: return
        val role = discordService.getRole(troop.role) ?: return

        if (!member.roles.contains(role)) {
            if (discordService.removeRole(member, role)) {
                player.player.orNull()?.sendMessage("- Removed Role ${role.name} from your Discord Account ${member.user.asTag} because you do not the required permission '${troop.permission}'".toText().red())
            }
        }
    }

    private fun getMember(player: MinecraftUser): Member? {
        val link = linkService.getLink(player) ?: return null

        return discordService.getMember(link.discordId)
    }

    override fun toString(): String {
        return javaClass.simpleName
    }
}