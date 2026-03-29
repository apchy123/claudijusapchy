package com.claudijusapchy.ratprotection

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object RatProtectionMod : ClientModInitializer {

    override fun onInitializeClient() {
        ModLogger.info("[RatProtection] Starting up...")
        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        ModLogger.success("[RatProtection] Active — blocking ${endpoints.size} patterns.")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("textshadow")
                    .then(literal("on").executes {
                        TextShadowConfig.shadowEnabled = true
                        ModLogger.info("[RatProtection] Text shadow: §aON")
                        1
                    })
                    .then(literal("off").executes {
                        TextShadowConfig.shadowEnabled = false
                        ModLogger.info("[RatProtection] Text shadow: §cOFF")
                        1
                    })
            )
        }
    }

    private fun loadEndpoints(): List<String> {
        return runCatching {
            val stream = javaClass.getResourceAsStream("/suspicious_endpoints.json")
                ?: return emptyList()
            stream.bufferedReader().readText()
                .trimStart('[').trimEnd(']')
                .split(",")
                .map { it.trim().trim('"') }
                .filter { it.isNotBlank() }
        }.getOrElse {
            ModLogger.warn("[RatProtection] Failed to load endpoint list: ${it.message}")
            emptyList()
        }
    }
}