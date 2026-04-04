package com.claudijusapchy.ratprotection

import com.claudijusapchy.ratprotection.config.ModConfig
import com.claudijusapchy.ratprotection.features.CommandAliases
import com.claudijusapchy.ratprotection.features.LagTracker
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import com.claudijusapchy.ratprotection.gui.ModScreen
import com.claudijusapchy.ratprotection.mixin.ContainerScreenAccessor
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import com.claudijusapchy.ratprotection.features.ZoomFeature
import com.mojang.authlib.minecraft.client.MinecraftClient
import com.claudijusapchy.ratprotection.features.UbikCubeTracker

object RatProtectionMod : ClientModInitializer {

    private val keyWasDown = mutableMapOf<Int, Boolean>()

    override fun onInitializeClient() {
        ModLogger.info("[RatProtection] Starting up...")
        ModConfig.load()

        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        PartyFinderRightClick.init()
        CommandAliases.init()

        ModLogger.success("[RatProtection] Active — blocking ${endpoints.size} patterns.")
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT  // not needed


// Add scroll handling:
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register { _ ->
            // already handled below
        }

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            LagTracker.tick()
            ZoomFeature.onClientTick(client)  // handles zoom key on its own

            val mc = Minecraft.getInstance()
            val screen = mc.screen
            if (screen !is AbstractContainerScreen<*>) return@register
            if (screen.title.string != "Party Finder") return@register

            val window = GLFW.glfwGetCurrentContext()

            val copyKey = ModConfig.copyLeaderKey
            if (copyKey != -1) {
                val isDown = PartyFinderRightClick.isKeyDown(window, copyKey)
                val wasDown = keyWasDown[copyKey] ?: false
                if (isDown && !wasDown) {
                    val slot = (screen as ContainerScreenAccessor).hoveredSlot
                    if (slot != null) PartyFinderRightClick.doCopyLeaderFromSlot(mc, slot)
                }
                keyWasDown[copyKey] = isDown
            }

            val leaveKey = ModConfig.leavePartyKey
            if (leaveKey != -1) {
                val isDown = PartyFinderRightClick.isKeyDown(window, leaveKey)
                val wasDown = keyWasDown[leaveKey] ?: false
                if (isDown && !wasDown) {
                    val slot = (screen as ContainerScreenAccessor).hoveredSlot
                    if (slot != null) PartyFinderRightClick.doLeavePartyFromSlot(mc, slot)
                }
                keyWasDown[leaveKey] = isDown
            }
            // ❌ remove the zoomKey block that was here
        }

        // ---- LAG TRACKER ----
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            val text = message.string
            if (text.contains("Motes in this match!")) {
                UbikCubeTracker.onMatchCompleted()
            }
            if (text.contains("Here, I found this map when I first entered the dungeon")) {

                LagTracker.start()
            }

            if (text.contains("Defeated Maxor, Storm, Goldor, and Necron in")) {
                val result = LagTracker.stop()
                if (result != null) {
                    Thread {
                        Thread.sleep(1000)
                        Minecraft.getInstance().execute {
                            val chat = Minecraft.getInstance().gui.chat
                            chat.addMessage(Component.literal("§8§m                                        "))
                            chat.addMessage(Component.literal(result))
                            chat.addMessage(Component.literal("§8§m                                        "))
                        }
                    }.start()
                }
            }
            }

        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register { guiGraphics, _ ->
            if (UbikCubeTracker.shouldShow()) {
                val mc = Minecraft.getInstance()
                guiGraphics.drawString(mc.font, "§aUbik Cube: Ready!", UbikCubeTracker.hudX, UbikCubeTracker.hudY, 0xFFFFFFFF.toInt(), true)
            }
        }


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
                    val mc = Minecraft.getInstance()
                    // We launch a tiny thread to wait for the chat UI to finish closing
                    Thread {
                        Thread.sleep(50)
                        mc.execute {
                            // Instantiate ModScreen() as a class now
                            mc.setScreen(ModScreen())
                        }
                    }.start()
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