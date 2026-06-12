package com.claudijusapchy.ratprotection

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object SkyblockUtils {
    var isOnHypixel = false
    var isOnSkyblock = false

    fun init() {
        ClientPlayConnectionEvents.JOIN.register { _, _, client ->
            val address = client.getCurrentServer()?.ip?.lowercase() ?: ""
            isOnHypixel = address.contains("hypixel.net")
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            isOnHypixel = false
            isOnSkyblock = false
        }
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (!isOnHypixel) {
                isOnSkyblock = false
                return@register
            }
            val scoreboard = client.level?.scoreboard ?: return@register
            isOnSkyblock = scoreboard.objectives.any {
                it.displayName.string.contains("SKYBLOCK", ignoreCase = true)
            }
        }
    }
}