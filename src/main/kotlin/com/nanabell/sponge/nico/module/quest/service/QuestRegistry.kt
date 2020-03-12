package com.nanabell.sponge.nico.module.quest.service

import com.google.common.reflect.TypeToken
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.quest.QuestModule
import com.nanabell.sponge.nico.module.quest.data.quest.Quest
import com.nanabell.sponge.nico.module.quest.data.quest.SimpleQuest
import com.nanabell.sponge.nico.module.quest.interfaces.IQuest
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.api.Sponge
import java.util.*
import kotlin.collections.ArrayList

@RegisterService
@Suppress("UnstableApiUsage")
class QuestRegistry : AbstractService<QuestModule>() {

    private val token = object : TypeToken<List<IQuest>>() {}
    private val quests: MutableList<IQuest> = ArrayList()

    private lateinit var loader: HoconConfigurationLoader
    private lateinit var node: ConfigurationNode

    override fun onPreEnable() {
        val questPath = Sponge.getConfigManager().getPluginConfig(plugin).directory.resolve("quest/quests.conf")
        loader = HoconConfigurationLoader.builder().setPath(questPath).build()
        node = loader.load()

        loadQuests()
    }


    fun loadQuests() {
        val questsNode = node.getNode("quests")
        if (questsNode.isVirtual) {
            saveQuests()
        }

        val quests = questsNode.getValue(token)
        if (quests == null)  {
            saveQuests()
            return
        }

        this.quests.addAll(quests)
    }

    fun saveQuests() {
        val toSave = quests.plus(defaults())
        var root: ConfigurationNode = node.getNode("quests")
        root.setValue(token, toSave)

        if (root.parent != null)
            root = root.parent!!

        node.mergeValuesFrom(root)
        loader.save(node)
    }

    private fun defaults(): List<Quest> {
        return listOf(
                SimpleQuest(
                        UUID.randomUUID(),
                        "Sample Quest",
                        "This is a Sample Quest",
                        listOf(UUID.randomUUID()),
                        listOf(UUID.randomUUID(), UUID.randomUUID()),
                        listOf()),
                SimpleQuest(
                        UUID.randomUUID(),
                        "Sample Quest 2",
                        "This is a Sample Quest 2",
                        listOf(UUID.randomUUID()),
                        listOf(UUID.randomUUID(), UUID.randomUUID()),
                        listOf()),
                SimpleQuest(
                        UUID.randomUUID(),
                        "Sample Quest 3",
                        "This is a Sample Quest 3",
                        listOf(UUID.randomUUID()),
                        listOf(UUID.randomUUID(), UUID.randomUUID()),
                        listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))
        )
    }
}