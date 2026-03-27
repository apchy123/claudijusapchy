package com.claudijusapchy.ratprotection

import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory
import java.util.UUID

object AuthEndpointSpammer {

    private val logger = LoggerFactory.getLogger("RatProtection-Auth")

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

                    logger.info("[RatProtection] Auth token invalidation ping sent.")

                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    // Expected to fail — intentional
                }
            }
        }, "RatProtection-AuthSpam").apply {
            isDaemon = true
            start()
        }
    }
}