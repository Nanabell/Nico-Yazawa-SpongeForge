package com.nanabell.sponge.nico.discordlink;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface LinkService {

    boolean pendingLink(@NotNull Long discordId);

    boolean isLinked(@NotNull Long discordId);

    boolean isLinked(@NotNull UUID minecraftId);

    LinkResult link(@NotNull Long discordId, @NotNull UUID minecraftId);

    LinkResult unlink(@NotNull Long discordId);

    LinkResult unlink(@NotNull UUID minecraftId);
}
