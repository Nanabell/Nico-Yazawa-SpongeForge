package com.nanabell.nico.nicoscoffee.storage;

import com.j256.ormlite.misc.BaseDaoEnabled;

import java.util.UUID;

public abstract class IdentifiableDaoEnabled<T> extends BaseDaoEnabled<T, UUID> {

    public abstract UUID getUniqueId();

    public abstract void setUniqueId(UUID uuid);
}
