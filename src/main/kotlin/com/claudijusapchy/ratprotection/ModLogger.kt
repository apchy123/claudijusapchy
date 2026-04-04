package com.claudijusapchy.ratprotection

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue

object ModLogger {
    private val logger = LoggerFactory.getLogger("RatProtection")
    private val pending = ConcurrentLinkedQueue<Pair<String, Int>>()

    init {
        Thread({
            while (true) {
                try {
                    Thread.sleep(100)
                    val mc = Minecraft.getInstance() ?: continue
                    val player = mc.player ?: continue
                    while (pending.isNotEmpty()) {
                        val (msg, color) = pending.poll() ?: break
                        mc.execute {
                            player.displayClientMessage(Component.literal(msg), false)
                        }
                    }
                } catch (_: Exception) {}
            }
        }, "RatProtection-Notify").apply { isDaemon = true; start() }
    }

    fun info(message: String) { logger.info(message); pending.add(Pair("§7$message", 0xFFFFFF)) }
    fun warn(message: String) { logger.warn(message); pending.add(Pair("§e$message", 0xFFFF00)) }
    fun block(message: String) { logger.warn(message); pending.add(Pair("§c$message", 0xFF0000)) }
    fun success(message: String) { logger.info(message); pending.add(Pair("§a$message", 0x00FF00)) }
}