package com.nanabell.sponge.nico.module.quest.service

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.serializer.CurrencySerializer
import com.nanabell.sponge.nico.internal.serializer.DurationSerializer
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.quest.DailyQuest
import com.nanabell.sponge.nico.module.quest.quest.Quest
import com.nanabell.sponge.nico.module.quest.quest.SimpleQuest
import com.nanabell.sponge.nico.module.quest.quest.WeeklyQuest
import com.nanabell.sponge.nico.module.quest.reward.MoneyReward
import com.nanabell.sponge.nico.module.quest.task.KillTask
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.economy.Currency
import java.time.Duration
import java.util.*

@Suppress("UnstableApiUsage")
@RegisterService
class QuestRegistry : AbstractService<QuestModule>() {

    private val token = object : TypeToken<List<Quest>>() {}
    private val serializers = TypeSerializers.getDefaultSerializers().newChild()
            .registerType(TypeToken.of(Currency::class.java), CurrencySerializer())
            .registerType(TypeToken.of(Duration::class.java), DurationSerializer())

    private lateinit var defaultLoader: HoconConfigurationLoader
    private lateinit var defaultRootNode: ConfigurationNode

    private lateinit var loader: HoconConfigurationLoader
    private lateinit var rootNode: ConfigurationNode

    override fun onPreEnable() {
        val path = Sponge.getConfigManager().getPluginConfig(NicoYazawa.getPlugin()).directory.resolve("quests.conf")
        loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(serializers))
                .setPath(path)
                .build()

        val defaultPath = Sponge.getConfigManager().getPluginConfig(NicoYazawa.getPlugin()).directory.resolve("default-quests.conf")
        defaultLoader = HoconConfigurationLoader.builder()
                .setDefaultOptions(ConfigurationOptions.defaults().setSerializers(serializers))
                .setPath(defaultPath)
                .build()

        rootNode = loader.load()
        defaultRootNode = defaultLoader.load()
    }

    override fun onEnable() {
        loadDefaults()
    }

    fun load(uniqueId: UUID): List<Quest> {
        val playerNode = rootNode.getNode(uniqueId.toString())
        if (playerNode.isVirtual) {
            save(uniqueId, loadDefaults())
        }

        val quests = playerNode.getValue(token)
        if (quests == null) {
            save(uniqueId, loadDefaults())
            return load(uniqueId)
        }

        val total = quests.toMutableList()
        loadDefaults().forEach { quest ->
            if (total.none { it.uniqueId == quest.uniqueId }) {
                total.add(quest)
            }
        }

        save(uniqueId, total)
        return quests
    }

    fun save(uniqueId: UUID, quests: List<Quest>) {
        var root: ConfigurationNode = rootNode.getNode(uniqueId.toString())
        root.setValue(token, quests)

        if (root.parent != null)
            root = root.parent!!

        rootNode.mergeValuesFrom(root)
        loader.save(rootNode)
    }

    fun loadDefaults(): List<Quest> {
        val questNode = defaultRootNode.getNode("quests")
        if (questNode.isVirtual) {
            saveDefaults(defaults())
        }

        val quests = questNode.getValue(token)
        if (quests == null) {
            saveDefaults(defaults())
            return loadDefaults()
        }

        return quests
    }

    fun saveDefaults(quests: List<Quest>) {
        val root: ConfigurationNode = defaultRootNode.getNode("quests")
        root.setValue(token, quests)

        defaultRootNode.mergeValuesFrom(root)
        defaultLoader.save(defaultRootNode)
    }

    // TODO: Move to Default serialized file once serialization stands
    fun defaults(): List<Quest> {
        return listOf(
                SimpleQuest.builder()
                        .setId(UUID.fromString("2e27a2b7-cea4-44fb-ae88-617a97d177fa"))
                        .setDescription("Kill a single hostile mob")
                        .addTask(KillTask.builder()
                                .setAmount(1)
                                .build())
                        .addReward(MoneyReward.builder()
                                .setAmount(100)
                                .build())
                        .build("Kill 1 Hostile Mob"),

                SimpleQuest.builder()
                        .setId(UUID.fromString("a82f48d5-a4fd-4a5b-b55d-8635477456f4"))
                        .setDescription("Kill 2 hostile mobs of any type")
                        .addTask(KillTask.builder()
                                .setAmount(2)
                                .build())
                        .addReward(MoneyReward.builder()
                                .setAmount(200)
                                .build())
                        .addRequirement(UUID.fromString("2e27a2b7-cea4-44fb-ae88-617a97d177fa"))
                        .build("Kill 2 Hostile Mobs"),

                DailyQuest.builder()
                        .setId(UUID.fromString("6073df2d-cb4d-4663-a75d-aaf09e159479"))
                        .setDescription("Kill 5 hostile mobs of any type")
                        .addTask(KillTask.builder()
                                .setAmount(5)
                                .build())
                        .addReward(MoneyReward.builder()
                                .setAmount(300)
                                .build())
                        .addRequirement(UUID.fromString("a82f48d5-a4fd-4a5b-b55d-8635477456f4"))
                        .build("Kill 5 Hostile Mobs"),

                WeeklyQuest.builder()
                        .setId(UUID.fromString("a46eae73-28f1-4260-aea1-1d551bf28e72"))
                        .setDescription("Kill 50 hostile mobs of any type")
                        .addTask(KillTask.builder()
                                .setAmount(50)
                                .build())
                        .addReward(MoneyReward.builder()
                                .setAmount(1000)
                                .build())
                        .addRequirement(UUID.fromString("6073df2d-cb4d-4663-a75d-aaf09e159479"))
                        .build("Kill 50 Hostile Mobs")


        )
    }
}
