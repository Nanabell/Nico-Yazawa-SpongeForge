package com.nanabell.sponge.nico.module.quest.data.reward

import com.nanabell.sponge.nico.NicoConstants
import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.internal.extension.*
import com.nanabell.sponge.nico.module.quest.interfaces.quest.IQuest
import com.nanabell.sponge.nico.module.quest.interfaces.reward.IReward
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.service.economy.Currency
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import java.util.*

@ConfigSerializable
class MoneyReward(
        id: UUID,

        @Setting("currency")
        private var _currency: String,

        @Setting("amount")
        var amount: Int

) : Reward(id) {

    var currency: Currency
        get() = economy.currencies.first { it.name == _currency }
        set(value) {
            _currency = value.name
        }

    @Suppress("unused")
    private constructor() : this(UUID.randomUUID(), "Maki Points", 0)

    private val economy: EconomyService = NicoYazawa.getServiceRegistry().provideUnchecked()
    private val userStore: UserStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService::class.java)

    override val type: String = "MoneyReward"

    // TODO: Add Return Value indicating if claim failed
    override fun reward(userId: UUID, cause: Cause) {
        val user = userStore[userId].orNull() ?: return
        val quest = cause.first(IQuest::class.java).orNull()
                ?: throw IllegalStateException("Missing Quest in Cause Stack")

        val account = economy.getOrCreateAccount(userId).orNull() ?: return
        if (!account.hasBalance(currency)) return

        val depositCause = Cause.of(EventContext.of(mapOf(EventContextKeys.OWNER to user)), this).with(cause)
        val result = account.deposit(currency, amount.toBigDecimal(), depositCause)
        if (result.result != ResultType.SUCCESS) return

        if (user.isOnline) {
            user.player.get().sendMessage(quest.getText()
                    .concat(NicoConstants.SPACE)
                    .concat("complete!".green())
                    .concat(Text.NEW_LINE)
                    .concat("Reward:".green())
                    .concat(NicoConstants.SPACE)
                    .concat(currency.format(amount.toBigDecimal())))
        }
    }

    override fun getName(): Text = "Money Reward".green()

    override fun getMessage(): Text = "Reward: ".yellow().concat(currency.format(amount.toBigDecimal()).yellow())

    override fun printSettings(): List<Text> {
        return listOf(
                "Currency: ".green().concat(currency.name.yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/reward edit currency $id "))),

                "Amount: ".green().concat(amount.toString().yellow()
                        .action(TextActions.showText("Click to edit...".gray()))
                        .action(TextActions.suggestCommand("/reward edit amount $id ")))
        )
    }

    override fun copy(id: UUID): IReward {
        return MoneyReward(id, _currency, amount)
    }
}