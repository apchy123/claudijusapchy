package com.claudijusapchy.ratprotection

import net.minecraft.client.Minecraft
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

object AuthEndpointSpammer {
    fun start() {
        Thread({
            while (true) {
                try {
                    Thread.sleep(60_000)
                    val mc = Minecraft.getInstance()
                    if (mc.level == null) continue
                    val fakeServerId = UUID.randomUUID().toString().replace("-", "")
                    val uuid = mc.user.profileId.toString().replace("-", "")
                    val token = mc.user.accessToken
                    val body = """{"accessToken":"$token","selectedProfile":"$uuid","serverId":"$fakeServerId"}"""
                    runCatching {
                        HttpClient.newHttpClient().send(
                            HttpRequest.newBuilder()
                                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/join"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(body))
                                .build(),
                            HttpResponse.BodyHandlers.ofString()
                        )
                    }
                    ModLogger.info("[RatProtection] Auth token invalidation ping sent.")
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) { }
            }
        }, "RatProtection-AuthSpam").apply { isDaemon = true; start() }
    }
}