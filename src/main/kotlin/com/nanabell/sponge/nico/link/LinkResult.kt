package com.nanabell.sponge.nico.link

import java.util.*

data class LinkResult(
        val state: LinkState,
        val link: Link?
) {
    companion object {
        fun error(state: LinkState) = LinkResult(state, null)

        fun success(state: LinkState = LinkState.LINKED, discordId: Long, minecraftId: UUID) = LinkResult(state, Link(discordId, minecraftId))

        fun success(discordId: Long, minecraftId: UUID) = LinkResult(LinkState.LINKED, Link(discordId, minecraftId))
    }
}