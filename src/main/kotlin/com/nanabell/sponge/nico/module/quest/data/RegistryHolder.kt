package com.nanabell.sponge.nico.module.quest.data

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.module.quest.service.QuestRegistry
import com.nanabell.sponge.nico.module.quest.service.RewardRegistry
import com.nanabell.sponge.nico.module.quest.service.TaskRegistry
import com.nanabell.sponge.nico.module.quest.service.UserRegistry

open class RegistryHolder {

    companion object {

        @JvmStatic
        val questRegistry: QuestRegistry by lazy { NicoYazawa.getServiceRegistry().provideUnchecked<QuestRegistry>() }

        @JvmStatic
        val taskRegistry: TaskRegistry by lazy { NicoYazawa.getServiceRegistry().provideUnchecked<TaskRegistry>() }

        @JvmStatic
        val userRegistry: UserRegistry by lazy { NicoYazawa.getServiceRegistry().provideUnchecked<UserRegistry>() }

        @JvmStatic
        val rewardRegistry: RewardRegistry by lazy { NicoYazawa.getServiceRegistry().provideUnchecked<RewardRegistry>() }

    }

}
