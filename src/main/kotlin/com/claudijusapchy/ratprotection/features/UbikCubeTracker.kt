package com.claudijusapchy.ratprotection.features

object UbikCubeTracker {

    @JvmField
    var enabled = true
    var hudX: Int = 10
    var hudY: Int = 10

    private const val COOLDOWN_MS = 2 * 60 * 60 * 1000L // 2 hours
    var lastCompletedAt: Long = 0L

    fun onMatchCompleted() {
        lastCompletedAt = System.currentTimeMillis()
        net.minecraft.client.Minecraft.getInstance().execute {
            com.claudijusapchy.ratprotection.config.ModConfig.save()
        }
    }g

    fun isReady(): Boolean {
        if (lastCompletedAt == 0L) return false
        return System.currentTimeMillis() - lastCompletedAt >= COOLDOWN_MS
    }

    fun shouldShow(): Boolean = enabled && lastCompletedAt != 0L && isReady()
    }