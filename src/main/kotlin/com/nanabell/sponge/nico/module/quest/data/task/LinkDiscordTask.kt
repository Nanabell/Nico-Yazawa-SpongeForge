package com.nanabell.sponge.nico.module.quest.data.task

import com.nanabell.sponge.nico.internal.extension.green
import com.nanabell.sponge.nico.internal.extension.yellow
import com.nanabell.sponge.nico.module.quest.data.user.TaskProgress
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITask
import com.nanabell.sponge.nico.module.quest.interfaces.task.ITaskProgress
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.text.Text
import java.util.*

@ConfigSerializable
class LinkDiscordTask(id: UUID) : Task(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID())

    override val type: String = "DiscordLinkTask"
    override fun newProgress(): ITaskProgress = LinkDiscordProgress(id, false)
    override fun getName(): Text = "Link Discord Task".green()
    override fun getMessage(): Text = "Link your Discord Account to your Minecraft Account".yellow()
    override fun printSettings(): List<Text> = emptyList()
    override fun copy(id: UUID): ITask = LinkDiscordTask(id)

}

@ConfigSerializable
class LinkDiscordProgress(
        id: UUID,

        @Setting("linked")
        var linked: Boolean

) : TaskProgress(id) {

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), false)

    override val type: String = "DiscordLinkTask"
    override fun getTask(): ITask = taskRegistry.get(id)

    override fun isComplete(): Boolean = linked
    override fun getText(): Text = "[${if (linked) "Linked" else "Not Linked"}]".yellow()

    override fun copy(id: UUID): ITaskProgress = LinkDiscordProgress(id, linked)

}
