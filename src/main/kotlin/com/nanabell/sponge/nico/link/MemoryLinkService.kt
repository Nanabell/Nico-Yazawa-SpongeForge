package com.nanabell.sponge.nico.link

import com.google.common.collect.HashBiMap
import com.nanabell.sponge.nico.*
import com.nanabell.sponge.nico.event.LinkEventContextKeys
import com.nanabell.sponge.nico.event.LinkRequestEvent
import com.nanabell.sponge.nico.event.LinkStateChangeEvent
import com.nanabell.sponge.nico.extensions.orNull
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



    override fun pendingLink(user: DiscordUser): Boolean {
        return pendingLinks.containsKey(user.idLong)
    }

    override fun pendingLink(user: MinecraftUser): Boolean {
        return pendingLinks.containsValue(user.uniqueId)
    }

    override fun isLinked(user: DiscordUser): Boolean {
        return links.containsKey(user.idLong)
    }

    override fun isLinked(user: MinecraftUser): Boolean {
        return links.containsValue(user.uniqueId)
    }

    override fun confirmLink(user: DiscordUser): LinkResult {
        if (isLinked(user)) return LinkResult.ALREADY_LINKED
        if (!pendingLink(user)) return LinkResult.NO_LINK_REQUEST

        links[user.idLong] = pendingLinks.remove(user.idLong)

        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, Cause.of(EventContext.empty(), this)))
        return LinkResult.SUCCESS
    }

    override fun confirmLink(user: MinecraftUser): LinkResult {
        if (isLinked(user)) return LinkResult.ALREADY_LINKED
        if (!pendingLink(user)) return LinkResult.NO_LINK_REQUEST

        links.inverse()[user.uniqueId] = pendingLinks.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.LINKED, Cause.of(EventContext.empty(), this)))

        return LinkResult.SUCCESS
    }

    override fun unlink(user: DiscordUser): LinkResult {
        if (!isLinked(user)) return LinkResult.NOT_LINKED

        links.remove(user.idLong)
        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, Cause.of(EventContext.empty(), this)))

        return LinkResult.SUCCESS
    }

    override fun unlink(user: MinecraftUser): LinkResult {
        if (!isLinked(user)) return LinkResult.NOT_LINKED

        links.inverse().remove(user.uniqueId)
        eventManager.post(LinkStateChangeEvent(LinkState.UNLINKED, Cause.of(EventContext.empty(), this)))

        return LinkResult.SUCCESS
    }

    @Listener
    fun onLinkRequest(event: LinkRequestEvent) {
        val context = event.cause.context
        val user = context.get(LinkEventContextKeys.USER).orNull()
        if (user == null) {
            logger.warn("OnLinkRequest did not include a context User.$event")
            return
        }

        if (pendingLink(user)) {
            logger.warn("Received LinkRequestEvent for user who already has a pending Request$event")
            return  // Already Pending Link
        }

        val player = Sponge.getServer().getPlayer(event.targetUserName).orNull()
        if (player == null) {
            val linkContext = EventContext.builder().add(LinkEventContextKeys.LINK_RESULT, LinkResult.USER_NOT_FOUND).build()
            val linkCause = Cause.builder().from(event.cause).append(this).build(linkContext)
            eventManager.post(LinkStateChangeEvent(LinkState.BROKEN, Cause.of(linkContext, linkCause)))

            context.get(LinkEventContextKeys.MESSAGE_CHANNEL).ifPresent { messageChannel: MessageChannel -> messageChannel.sendMessage(event.targetUserName + " is not online.\nProvide the name of an online player.").queue() }
            return
        }

        pendingLinks[user.idLong] = player.uniqueId
        val msg: Text = Text.builder("Incoming Discord link request: ").color(TextColors.BLUE)
                .append(Text.of(user.asTag + " "))
                .append(Text.of(TextColors.GREEN, TextActions.runCommand("/nico link accept"), "[Confirm]"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.RED, TextActions.runCommand("/nico link deny"), "[Deny]"))
                .build()
        player.sendMessage(msg)
    }

    init {
        Sponge.getEventManager().registerListeners(plugin, this)
    }
}