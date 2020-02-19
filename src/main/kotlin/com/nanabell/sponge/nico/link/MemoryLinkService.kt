package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.extensions.DiscordUser
import com.nanabell.sponge.nico.extensions.MinecraftUser
import com.nanabell.sponge.nico.extensions.orNull
import com.nanabell.sponge.nico.extensions.toText
import com.nanabell.sponge.nico.link.event.LinkEventContextKeys
import com.nanabell.sponge.nico.link.event.LinkRequestEvent
import com.nanabell.sponge.nico.link.event.LinkStateChangeEvent
import net.dv8tion.jda.api.entities.MessageChannel
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import java.util.*

class MemoryLinkService(plugin: NicoYazawa) : LinkService {

    private val logger = NicoYazawa.getLogger()
    private val eventManager = Sponge.getEventManager()

    private val pendingLinks = HashBiMap.create<Long, UUID>()
    private val links = HashBiMap.create<Long, UUID>()


    override fun isPending(user: DiscordUser): Boolean {
        return pendingLinks.containsKey(user.idLong)
    }

    override fun isPending(user: MinecraftUser): Boolean {
        return pendingLinks.containsValue(user.uniqueId)
    }

    override fun isLinked(user: MinecraftUser): Boolean {
        return links.containsValue(user.uniqueId)
    }

    override fun getLink(user: MinecraftUser): Link? {
        if (!isLinked(user)) return null

        return Link(links.inverse()[user.uniqueId]!!, user.uniqueId)
    }

    override fun removePending(user: MinecraftUser): Boolean {
        return pendingLinks.inverse().remove(user.uniqueId) != null
    }

    override fun confirmLink(user: MinecraftUser, cause: Cause): LinkResult {
        if (isLinked(user)) return LinkResult.error(LinkState.ALREADY_LINKED)
        if (!isPending(user)) return LinkResult.error(LinkState.NO_LINK_REQUEST)

        links.inverse()[user.uniqueId] = pendingLinks.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, cause))

        return LinkResult.success(links.inverse()[user.uniqueId]!!, user.uniqueId)
    }

    override fun unlink(user: MinecraftUser, cause: Cause): LinkResult {
        if (!isLinked(user)) return LinkResult.error(LinkState.UNLINKED)

        val id = links.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, cause))

        return LinkResult.success(LinkState.UNLINKED, id!!, user.uniqueId)
    }

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        val context = event.cause.context
        val user = context.get(LinkEventContextKeys.USER).orNull()
        if (user == null) {
            logger.warn("OnLinkRequest did not include a context User. $event")
            return
        }

        if (isPending(user)) {
            logger.warn("Received LinkRequestEvent for user who already has a pending Request $event")
            return  // Already Pending Link
        }

        val player = Sponge.getServer().getPlayer(event.target).orNull()
        if (player == null) {
            eventManager.post(LinkStateChangeEvent(LinkState.USER_NOT_FOUND, Cause.of(EventContext.empty(), this)))

            context.get(LinkEventContextKeys.MESSAGE_CHANNEL).ifPresent { messageChannel: MessageChannel ->
                messageChannel.sendMessage(event.target + " is not online.\nProvide the name of an online player.").queue()
            }
            return
        }

        pendingLinks[user.idLong] = player.uniqueId
        val msg: Text = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.asTag + " "))
                .append(Text.of(TextColors.GREEN,
                        TextActions.runCommand("/nico link accept"),
                        TextActions.showText("/nico link accept".toText()),
                        "[Confirm]"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED,
                        TextActions.runCommand("/nico link deny"),
                        TextActions.showText("/nico link deny".toText()),
                        "[Deny]"))
                .build()
        player.sendMessage(msg)
    }

    init {
        Sponge.getEventManager().registerListeners(plugin, this)
    }
}