package com.nanabell.sponge.nico.module.quest.data.quest

import com.nanabell.sponge.nico.module.quest.quest.QuestStatus
import java.util.*

class QuestProgress(
        val userId: UUID,
        val questId: UUID,
        val status: QuestStatus,
        val tasks: List<UUID>,
        val dependencies: List<UUID>
) {

}