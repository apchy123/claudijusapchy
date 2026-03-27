package com.claudijusapchy.ratprotection

import net.fabricmc.api.ClientModInitializer

object RatProtectionMod : ClientModInitializer {

    override fun onInitializeClient() {
        ModLogger.info("[RatProtection] Starting up...")
        val endpoints = loadEndpoints()
        RatProxySelector.install(endpoints)
        AuthEndpointSpammer.start()
        ModLogger.success("[RatProtection] Active — blocking ${endpoints.size} patterns.")
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