package com.nanabell.sponge.nico.storage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.UUID;

public class Persistable<T extends IdentifiableDaoEnabled<T>> {

    private Dao<T, UUID> dao;

    public Persistable(@NotNull String url, @NotNull Class<T> clazz) {
        try {
            JdbcConnectionSource source = new JdbcConnectionSource(url);
            dao = DaoManager.createDao(source, clazz);

            TableUtils.createTableIfNotExists(source, clazz);
        } catch (SQLException e) {
            e.printStackTrace(); //TODO: Handle
        }
    }

    public void save(@NotNull T object) {
        if (object.getDao() == null) {
            object.setDao(this.dao);
        }

        try {
            this.dao.createOrUpdate(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(@NotNull T object) {
        try {
            this.dao.delete(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(@NotNull UUID id) {
        try {
            return dao.idExists(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    public T get(@NotNull UUID id) {
        try {
            return this.dao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    public T getOrCreate(@NotNull UUID id) {
        try {
            T object = this.get(id);
            if (object == null) {
                object = this.dao.getDataClass().newInstance();
                object.setUniqueId(id);

                this.save(object);
            }
            return object;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to construct an instance of " + this.get(id));
        }
    }

    public Class<T> getDaoClass() {
        return this.dao.getDataClass();
    }
}
