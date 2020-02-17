package com.nanabell.nico.nicoscoffee.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public class Config<T> {

    private HoconConfigurationLoader loader;
    private Class<T> clazz;
    private TypeToken<T> token;
    private T config;

    public Config(Class<T> clazz, String name, Path configDir) {
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectories(configDir);
            }

            Path file = configDir.resolve(name);
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            this.clazz = clazz;
            this.token = TypeToken.of(clazz);
            this.loader = HoconConfigurationLoader.builder().setPath(file).build();
            this.config = load();

            if (Files.size(file) == 0) {
                save();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T load() {
        try {
            CommentedConfigurationNode node = this.loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            return node.getNode("config").getValue(token, clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void reload() {
        this.config = load();
    }

    public T get() {
        return this.config;
    }

    public void save() {
        try {
            CommentedConfigurationNode node = this.loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            node.getNode("config").setValue(token, this.config);

            this.loader.save(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
