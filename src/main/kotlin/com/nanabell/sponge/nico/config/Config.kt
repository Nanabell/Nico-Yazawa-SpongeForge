package com.nanabell.sponge.nico.config

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Files
import java.nio.file.Path

@Suppress("UnstableApiUsage")
class Config<T>(clazz: Class<T>, name: String, configDir: Path) {

    private var loader: HoconConfigurationLoader
    private val clazz: Class<T>
    private var token: TypeToken<T>
    private var config: T

    init {
        if (Files.notExists(configDir)) {
            Files.createDirectories(configDir)
        }

        val file = configDir.resolve(name)
        if (Files.notExists(file)) {
            Files.createFile(file)
        }

        this.clazz = clazz
        this.token = TypeToken.of(clazz)
        this.loader = HoconConfigurationLoader.builder().setPath(file).build()
        this.config = load()

        if (Files.size(file) == 0L) {
            save()
        }
    }

    private fun load(): T {
        val node = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true))
        return node.getNode("config").getValue(token, clazz.newInstance())
    }

    fun reload() {
        config = load()
    }

    fun get(): T {
        return config
    }

    private fun save() {
        val node = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true))
        node.getNode("config").setValue(token, config)

        loader.save(node)
    }


}