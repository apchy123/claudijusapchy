package com.claudijusapchy.ratprotection

import com.claudijusapchy.ratprotection.config.ModConfig
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
object RatProtectionMod : ClientModInitializer {

    override fun onInitializeClient() {
        ModLogger.info("[RatProtection] Starting up...")

        // Load saved config first
        ModConfig.load()

        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        PartyFinderRightClick.init()

        ModLogger.success("[RatProtection] Active — blocking ${endpoints.size} patterns.")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

            // /claude ts on|off
            dispatcher.register(
                literal("claude")
                    .then(literal("ts")
                        .then(literal("on").executes {
                            TextShadowConfig.shadowEnabled = true
                            ModConfig.save()
                            ModLogger.info("[RatProtection] Text shadow: §aON")
                            1
                        })
                        .then(literal("off").executes {
                            TextShadowConfig.shadowEnabled = false
                            ModConfig.save()
                            ModLogger.info("[RatProtection] Text shadow: §cOFF")
                            1
                        })
                    )
                    .then(literal("copyleader").executes {
                        PartyFinderRightClick.mode = 0
                        ModConfig.save()
                        1
                    })
                    .then(literal("leaveparty").executes {
                        PartyFinderRightClick.mode = 1
                        ModConfig.save()
                        1
                    })
                    .then(literal("mode").executes {
                        val current = if (PartyFinderRightClick.mode == 0) "§aCopyLeader" else "§eLeaveParty"
                        ModLogger.info("[RatProtection] Party Finder mode: $current")
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