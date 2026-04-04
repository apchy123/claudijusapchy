package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.config.ModConfig
import org.lwjgl.glfw.GLFW
import kotlin.math.pow
import net.minecraft.client.Minecraft

object ZoomFeature {

    private const val MAX_STEPS = 100.0
    private var currentStep = MAX_STEPS

    @JvmField
    var enabled = true
    @JvmField
    var isZooming = false

    @JvmField
    var cachedFactor = 1f

    fun onScroll(yOffset: Double) {
        val delta = if (yOffset > 0) -5.0 else 5.0  // flip the signs
        update(currentStep + delta)
    }
    private fun ease(x: Double) = x.pow(3)

    private fun update(step: Double) {
        currentStep = step.coerceIn(0.0, MAX_STEPS)
        // This is the rescale logic from the other mod
        val rescaled = (currentStep - 0.0) / (MAX_STEPS - 0.0) * (1.0 - 0.1) + 0.1
        cachedFactor = ease(rescaled).toFloat()
    }

    fun onKeyPress() {
        if (isZooming) return
        isZooming = true
        update(MAX_STEPS * 0.75) // Initial zoom jump
    }

    fun onKeyRelease() {
        isZooming = false
        update(MAX_STEPS) // Reset to no zoom
    }

    fun onClientTick(client: Minecraft) {
        val windowHandle = GLFW.glfwGetCurrentContext()

        if (!enabled || ModConfig.zoomKey == -1) {
            if (isZooming) onKeyRelease()
            return
        }

        // Don't zoom if any screen is open
        if (client.screen != null) {
            if (isZooming) onKeyRelease()
            return
        }

        val key = ModConfig.zoomKey
        val isKeyDown = if (key < 0) {
            val mouseBtn = (key * -1) - 2
            GLFW.glfwGetMouseButton(windowHandle, mouseBtn) == GLFW.GLFW_PRESS
        } else {
            GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS
        }

        if (isKeyDown) onKeyPress() else if (isZooming) onKeyRelease()
    }
}