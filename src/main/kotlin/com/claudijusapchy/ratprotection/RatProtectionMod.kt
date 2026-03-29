package com.claudijusapchy.ratprotection

import com.claudijusapchy.ratprotection.config.ModConfig
import com.claudijusapchy.ratprotection.features.CommandAliases
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.arguments.StringArgumentType
import com.claudijusapchy.ratprotection.gui.ModScreen
import net.minecraft.client.Minecraft
val minecraft = Minecraft.getInstance()

object RatProtectionMod : ClientModInitializer {

    override fun onInitializeClient() {
        ModLogger.info("[RatProtection] Starting up...")
        ModConfig.load()

        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        PartyFinderRightClick.init()
        CommandAliases.init()

        ModLogger.success("[RatProtection] Active — blocking ${endpoints.size} patterns.")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
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
                    .then(literal("pf")
                        .then(literal("copyleader").executes {
                            PartyFinderRightClick.mode = 0
                            ModConfig.save()
                            ModLogger.info("[RatProtection] Party Finder mode: §aCopyLeader")
                            1
                        })
                        .then(literal("leaveparty").executes {
                            PartyFinderRightClick.mode = 1
                            ModConfig.save()
                            ModLogger.info("[RatProtection] Party Finder mode: §eLeaveParty")
                            1
                        })
                        .then(literal("mode").executes {
                            val current = if (PartyFinderRightClick.mode == 0) "§aCopyLeader" else "§eLeaveParty"
                            ModLogger.info("[RatProtection] Party Finder mode: $current")
                            1
                        })
                    )
                    .then(literal("alias")
                        .then(literal("add")
                            .then(argument("alias", StringArgumentType.word())
                                .then(argument("command", StringArgumentType.greedyString())
                                    .executes { ctx ->
                                        val alias = StringArgumentType.getString(ctx, "alias")
                                        val command = StringArgumentType.getString(ctx, "command")
                                        CommandAliases.addAlias(alias, command)
                                        ModConfig.save()
                                        1
                                    }
                                )
                            )
                        )
                        .then(literal("remove")
                            .then(argument("alias", StringArgumentType.word())
                                .executes { ctx ->
                                    val alias = StringArgumentType.getString(ctx, "alias")
                                    CommandAliases.removeAlias(alias)
                                    ModConfig.save()
                                    1
                                }
                            )
                        )
                        .then(literal("list").executes {
                            CommandAliases.listAliases()
                            1
                        })
                    )
                    .then(literal("gui").executes {
                        minecraft.execute {
                            minecraft.setScreen(ModScreen)
                        }
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