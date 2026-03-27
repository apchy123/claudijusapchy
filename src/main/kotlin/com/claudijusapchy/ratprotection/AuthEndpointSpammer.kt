package com.claudijusapchy.ratprotection

import net.minecraft.client.Minecraft
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
                    runCatching {
                        mc.minecraftSessionService.joinServer(
                            mc.user.getProfileId(),
                            mc.user.getAccessToken(),
                            fakeServerId
                        )
                    }
                    ModLogger.info("[RatProtection] Auth token invalidation ping sent.")
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    // Expected to fail
                }
            }
        }, "RatProtection-AuthSpam").apply {
            isDaemon = true
            start()
        }
    }
}