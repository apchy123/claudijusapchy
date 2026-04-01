package com.claudijusapchy.ratprotection.features

object LagTracker {
    private var startTime = 0L
    private var packetCount = 0
    private var tracking = false

    fun start() {
        startTime = System.currentTimeMillis()
        packetCount = 0
        tracking = true
    }

    fun countPacket() {
        if (tracking) packetCount++
    }

    fun tick() {} // keep this so RatProtectionMod.kt doesn't break

    fun stop(): String? {
        if (!tracking) return null
        tracking = false
        val realSeconds = (System.currentTimeMillis() - startTime) / 1000.0
        val packetSeconds = packetCount / 20.0
        val lag = (realSeconds - packetSeconds).coerceAtLeast(0.0)
        return "§8[§6Lag§8] §f%.2fs §7lost to server lag in boss".format(lag)
    }
}