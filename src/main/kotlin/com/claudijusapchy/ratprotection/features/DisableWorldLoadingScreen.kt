package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.mixin.LevelLoadingScreenAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.LevelLoadingScreen
import net.minecraft.client.gui.screens.Overlay
import net.minecraft.client.gui.screens.Screen

object DisableWorldLoadingScreen {

    var enabled = true
        @JvmName("getEnabled")
        get() = field

    private val mc get() = Minecraft.getInstance()
    private var levelLoadingScreen: LevelLoadingScreenAccessor? = null

    fun onScreenOpen(screen: Screen?) {
        if (!enabled) return
        if (screen !is LevelLoadingScreen) {
            if (mc.overlay is PausingOverlay) mc.overlay = null
            return
        }
        levelLoadingScreen = screen as? LevelLoadingScreenAccessor
        mc.setScreen(null)
        mc.overlay = PausingOverlay
    }

    fun onPlayerLoaded() {
        mc.overlay = null
    }

    object PausingOverlay : Overlay() {
        override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {}
        override fun tick() {
            val screen = levelLoadingScreen ?: return
            if (screen.loadTracker.isLevelReady || (mc.singleplayerServer?.isReady == true)) {
                mc.overlay = null
            }
        }
    }
}