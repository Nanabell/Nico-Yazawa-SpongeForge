package com.nanabell.sponge.nico.module.core.command

import com.nanabell.sponge.nico.internal.annotation.command.Permissions
import com.nanabell.sponge.nico.internal.annotation.command.RegisterCommand
import com.nanabell.sponge.nico.internal.command.AbstractCommand
import com.nanabell.sponge.nico.internal.extension.orNull
import com.nanabell.sponge.nico.module.core.CoreModule
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import java.util.*


@Permissions
@RegisterCommand(["dummy"], NicoCommand::class)
class DummyCommand : AbstractCommand<CommandSource, CoreModule>() {

    override fun executeCommand(source: CommandSource, args: CommandContext, cause: Cause): CommandResult {
        val item = (source as Player).getItemInHand(HandTypes.MAIN_HAND).orNull() ?: return CommandResult.success()

        source.sendMessage(buildItemName(item))


        return CommandResult.success()
    }

    override fun getDescription(): String {
        return "Admin Dummy Command. be careful to use this. This might literally do anything!!"
    }

    private fun buildItemName(itemStack: ItemStack): Text {
        val displayName: Text
        var itemColor: TextColor?
        val displayNameOptional: Optional<Text> = itemStack.get(Keys.DISPLAY_NAME)

        // If the item has a display name, we'll use that
        if (displayNameOptional.isPresent) {
            displayName = displayNameOptional.get()
            itemColor = displayName.color
            if (!displayName.children.isEmpty()) {
                itemColor = displayName.children[0].color
            }
        } else { // Just grab the item name
            displayName = Text.of(itemStack.translation)
            itemColor = displayName.color

            // Color the item aqua if it has an enchantment
            if (itemStack.get(EnchantmentData::class.java).isPresent) {
                itemColor = TextColors.AQUA
            }
        }

        // Build the item text with the color
        return Text.builder().color(itemColor ?: TextColors.DARK_RED)
                .append(Text.of("["), displayName, Text.of("]"))
                .onHover(TextActions.showItem(itemStack.createSnapshot())).build()
    }
}