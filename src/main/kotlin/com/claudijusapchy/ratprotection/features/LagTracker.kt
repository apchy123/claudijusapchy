package com.claudijusapchy.ratprotection.features

object LagTracker {
    private var totalLagMs = 0L
    private var lastPacketTime = 0L
    private var tracking = false

    fun start() {
        totalLagMs = 0L
        lastPacketTime = System.currentTimeMillis()
        tracking = true
    }

    fun countPacket() {
        if (!tracking) return
        val now = System.currentTimeMillis()
        val diff = now - lastPacketTime
        val expected = 500L
        if (diff > expected) totalLagMs += (diff - expected)
        lastPacketTime = now
    }
    fun tick() {}

    fun stop(): String? {
        if (!tracking) return null
        tracking = false
        return "§8[§6CA§8] §f%.2fs §7lost to server lag this run".format(totalLagMs / 1000.0)
    }
}