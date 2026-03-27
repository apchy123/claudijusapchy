package com.claudijusapchy.ratprotection

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.slf4j.LoggerFactory

object ModLogger {

    private val logger = LoggerFactory.getLogger("RatProtection")

    fun info(message: String) {
        logger.info(message)
        sendChat("§7$message")  // grey
    }

    fun warn(message: String) {
        logger.warn(message)
        sendChat("§e$message")  // yellow
    }

    fun block(message: String) {
        logger.warn(message)
        sendChat("§c$message")  // red
    }

    fun success(message: String) {
        logger.info(message)
        sendChat("§a$message")  // green
    }

    private fun sendChat(message: String) {
        Thread({
            repeat(30) {
                Thread.sleep(500)
                val mc = Minecraft.getInstance()
                val player = mc.player
                if (player != null) {
                    mc.execute {
                        player.displayClientMessage(
                            Component.literal(message), false
                        )
                    }
                    return@Thread
                }
            }
        }, "RatProtection-Notify").apply { isDaemon = true; start() }
    }
}