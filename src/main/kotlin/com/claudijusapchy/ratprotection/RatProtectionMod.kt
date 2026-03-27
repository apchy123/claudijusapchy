package com.claudijusapchy.ratprotection

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object RatProtectionMod : ClientModInitializer {

    private val logger = LoggerFactory.getLogger("RatProtection")

    override fun onInitializeClient() {
        logger.info("[RatProtection] Starting up...")
        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        logger.info("[RatProtection] Active — blocking ${endpoints.size} patterns.")
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
            logger.error("[RatProtection] Failed to load endpoint list: ${it.message}")
            emptyList()
        }
    }
}