package com.nanabell.sponge.nico.command.admin

import com.nanabell.sponge.nico.NicoYazawa
import com.nanabell.sponge.nico.command.SelfSpecCommand
import com.nanabell.sponge.nico.extensions.toText
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.permission.PermissionDescription

class DummyCommand : CommandExecutor, SelfSpecCommand {

    override fun aliases(): Array<String> = arrayOf("dummy")

    override fun spec(): CommandSpec {
        return CommandSpec.builder()
                .permission("nico.command.admin")
                .description("Admin Dummy Command used for Testing.\nDo not use unless you know what you are doing!\nThis Command might literally do anything".toText())
                .executor(this)
                .build()
    }

    override fun permissionDescriptions(builder: PermissionDescription.Builder) {
        builder.id("nico.command.admin").register()
    }

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {

        NicoYazawa.getPlugin().getConfig().get().discordLinkConfig.syncConfig.troops.forEach {
            src.sendMessage("${it.key}  = ${it.value.joinToString(", ")}".toText())
        }

        return CommandResult.success()
    }
}