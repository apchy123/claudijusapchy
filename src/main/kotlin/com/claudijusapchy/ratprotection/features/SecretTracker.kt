package com.claudijusapchy.ratprotection.features

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents



object SecretTracker {
    @JvmField var enabled = true
    @JvmField var hudX: Int = 10
    @JvmField var hudY: Int = 40
    @JvmField var scale: Float = 1.0f
    @JvmField var visible = false
    @JvmField var foundN = 0
    @JvmField var totalN = 0
    var lastSeenTick = 0L

    fun initialize() {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register { _ ->
            if (visible && System.currentTimeMillis() - lastSeenTick > 1000) {
                visible = false
                foundN = 0
                totalN = 0
            }
        }
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            visible = false
            foundN = 0
            totalN = 0
        }
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            if (!overlay) return@register
            val str = message.string
            if (!str.contains("/") || !str.contains("Secrets")) return@register

            val pieces = str.split("\\s{2,}".toRegex())
            val secretPiece = pieces.find { it.endsWith(" Secrets") } ?: return@register

            val ss = secretPiece.dropLast(" Secrets".length)
            val match = "(\\d+)/(\\d+)".toRegex()
                .find(ss.replace("§7", "")) ?: return@register

            val (_, found, total) = match.groupValues
            foundN = found.toIntOrNull() ?: return@register
            totalN = total.toIntOrNull() ?: return@register
            visible = true
            lastSeenTick = System.currentTimeMillis()
        }
    }}