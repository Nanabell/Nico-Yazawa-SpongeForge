package com.nanabell.nico.nicoscoffee.config;

import com.google.inject.internal.cglib.core.$ClassNameReader;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DiscordLinkConfig {

    @Setting(comment = "Discord Bot Token")
    private String token = "DISCORD_TOKEN_HERE";

    @Setting(value = "link_guild_id", comment = "Snowflake Id of the Discord Guild")
    private Long guildId = -1L;

    @Setting(value = "link_message_id", comment = "Snowflake ID of the Discord Message")
    private Long messageId = -1L;

    @Setting(value = "reaction_emote", comment = "Reaction Emote which will be used to link Accounts")
    private String reactionEmote = "U+1f517";

    @Setting(value = "link_role", comment = "The Discord Role that should be given if an account was linked successfully (-1 to disable)")
    private Long linkRole = -1L;

    @Setting(value = "link_group", comment = "The Minecraft Permission Group that should be awarded upon successful linking (empty to disable)")
    private String linkGroup;


    public String getToken() {
        return token;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getReactionEmote() {
        return reactionEmote;
    }

    public Long getLinkRole() {
        return linkRole;
    }

    public String getLinkGroup() {
        return linkGroup;
    }
}
