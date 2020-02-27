package com.nanabell.sponge.nico.module.link.config

import com.nanabell.sponge.nico.internal.config.Config
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
data class LinkConfig(

        @Setting("channel-id", comment = "The Channel of the Message Id")
        val channelId: Long = -1,

        @Setting("message-id", comment = "The Message Id which will be watched for Reactions")
        val messageId: Long = -1,

        @Setting("reaction-emote", comment = "The Reaction emote to watch out for. This emote will be added to the Message if not existing.")
        val reactionEmote: String = "",

        @Setting("link-permission", comment = "Minecraft Permission which is awarded upon Link. Will be revoked if the Link is broken")
        val linkPermission: String = "",

        @Setting("link-role", comment = "Discord Role Id which is awarded upon Link. Will be revoked if the Link is broken")
        val linkRole: Long = -1

) : Config