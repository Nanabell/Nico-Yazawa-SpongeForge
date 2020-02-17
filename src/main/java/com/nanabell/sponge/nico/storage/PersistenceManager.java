package com.nanabell.sponge.nico.storage;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PersistenceManager {

    @SuppressWarnings("rawtypes")
    private Map<Class<? extends IdentifiableDaoEnabled<?>>, Persistable> persistableMap = new HashMap<>();

    public <T extends IdentifiableDaoEnabled<T>> void register(@NotNull Persistable<T> persistable) {
        persistableMap.put(persistable.getDaoClass(), persistable);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableDaoEnabled<T>> Persistable<T> get(Class<T> clazz) {
        return (Persistable<T>) persistableMap.get(clazz);
    }

    public <T extends IdentifiableDaoEnabled<T>> Persistable<T> getUnchecked(Class<T> clazz) {
        return get(clazz);
    }
}
