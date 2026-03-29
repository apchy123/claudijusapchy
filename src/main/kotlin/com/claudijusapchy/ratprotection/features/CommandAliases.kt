package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.ModLogger
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents

object CommandAliases {

    // alias -> command (without slash)
    val aliases = mutableMapOf<String, String>()

    fun init() {
        ClientSendMessageEvents.ALLOW_COMMAND.register { msg ->
            val parts = msg.trim().split(" ")
            val cmd = parts[0].lowercase()
            val args = parts.drop(1).joinToString(" ")

            val mapped = aliases[cmd] ?: return@register true

            // Schedule on next tick to avoid event recursion
            Thread {
                Thread.sleep(50)
                net.minecraft.client.Minecraft.getInstance().execute {
                    val fullCmd = if (args.isBlank()) mapped else "$mapped $args"
                    net.minecraft.client.Minecraft.getInstance().player
                        ?.connection?.sendCommand(fullCmd)
                }
            }.apply { isDaemon = true; start() }

            false // cancel original command
        }
    }

    fun addAlias(alias: String, command: String) {
        aliases[alias.lowercase()] = command.removePrefix("/")
        ModLogger.success("[RatProtection] Alias added: §a/$alias §7-> §a/$command")
    }

    fun removeAlias(alias: String) {
        if (aliases.remove(alias.lowercase()) != null) {
            ModLogger.success("[RatProtection] Alias removed: §a/$alias")
        } else {
            ModLogger.warn("[RatProtection] Alias not found: $alias")
        }
    }

    fun listAliases() {
        if (aliases.isEmpty()) {
            ModLogger.info("[RatProtection] No aliases set.")
            return
        }
        ModLogger.info("[RatProtection] Aliases:")
        aliases.forEach { (alias, command) ->
            ModLogger.info("  §a/$alias §7-> §a/$command")
        }
    }
}