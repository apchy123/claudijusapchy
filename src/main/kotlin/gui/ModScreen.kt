package com.claudijusapchy.ratprotection.gui

import com.claudijusapchy.ratprotection.TextShadowConfig
import com.claudijusapchy.ratprotection.config.ModConfig
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class ModScreen : Screen(Component.literal("Rat Protection")) {

    private fun renderOutline(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Int) {
        guiGraphics.fill(x, y, x + width, y + 1, color)
        guiGraphics.fill(x, y + height - 1, x + width, y + height, color)
        guiGraphics.fill(x, y, x + 1, y + height, color)
        guiGraphics.fill(x + width - 1, y, x + width, y + height, color)
    }

    data class FeatureButton(
        val label: String,
        val isEnabled: () -> Boolean,
        val toggle: () -> Unit,
        val column: Int,
        val row: Int,
        val dropdownOptions: List<DropdownOption> = emptyList()
    )

    data class DropdownOption(
        val label: String,
        val isEnabled: () -> Boolean,
        val toggle: () -> Unit,
        val keybindKey: String? = null
    )

    private val features = listOf(
        FeatureButton(
            label = "Text Shadow",
            isEnabled = { TextShadowConfig.shadowEnabled },
            toggle = {
                TextShadowConfig.shadowEnabled = !TextShadowConfig.shadowEnabled
                ModConfig.save()
            },
            column = 0, row = 0
        ),
        FeatureButton(
            label = "Party Finder",
            isEnabled = { PartyFinderRightClick.enabled },
            toggle = {
                PartyFinderRightClick.enabled = !PartyFinderRightClick.enabled
                ModConfig.save()
            },
            column = 0, row = 1,
            dropdownOptions = listOf(
                DropdownOption(
                    label = "Copy Leader",
                    isEnabled = { PartyFinderRightClick.copyLeaderEnabled },
                    toggle = {
                        PartyFinderRightClick.copyLeaderEnabled = !PartyFinderRightClick.copyLeaderEnabled
                        ModConfig.save()
                    },
                    keybindKey = "copyLeader"
                ),
                DropdownOption(
                    label = "Leave Party",
                    isEnabled = { PartyFinderRightClick.leavePartyEnabled },
                    toggle = {
                        PartyFinderRightClick.leavePartyEnabled = !PartyFinderRightClick.leavePartyEnabled
                        ModConfig.save()
                    },
                    keybindKey = "leaveParty"
                )
            )
        ),
        FeatureButton(
            label = "Rat Protection",
            isEnabled = { true },
            toggle = {},
            column = 0, row = 2
        )
    )

    companion object {
        private const val COLUMN_WIDTH = 160
        private const val BUTTON_HEIGHT = 20
        private const val BUTTON_PADDING = 2
        private const val COLUMN_HEADER_HEIGHT = 24
        private const val COLUMN_START_X = 20
        private const val COLUMN_START_Y = 20

        // Colors - using Long to avoid Int overflow/transparency issues in 1.21.1
        private const val ENABLED_COLOR = 0xFF0078D7.toInt()
        private const val DISABLED_COLOR = 0xFF3C3C3C.toInt()
        private const val HEADER_COLOR = 0xF0141414.toInt()
        private const val DROPDOWN_COLOR = 0xFF282828.toInt()
        private const val DROPDOWN_ENABLED_COLOR = 0xFF005AA0.toInt()
        private const val DROPDOWN_HOVER_COLOR = 0xFF0064B4.toInt()
        private const val TEXT_COLOR = 0xFFFFFFFF.toInt()
        private const val BORDER_COLOR = 0xFF505050.toInt()
        private const val KEYBIND_COLOR = 0xFF323232.toInt()
        private const val KEYBIND_BINDING_COLOR = 0xFFB43C3C.toInt()
    }

    private var hoveredButton: FeatureButton? = null
    private var hoveredDropdownIndex: Int = -1
    private var openDropdown: FeatureButton? = null
    private var bindingFeature: String? = null
    private var bindingMode: Boolean = false

    private fun getKeybindValue(key: String): Int = when (key) {
        "copyLeader" -> ModConfig.copyLeaderKey
        "leaveParty" -> ModConfig.leavePartyKey
        else -> -1
    }

    private fun setKeybindValue(key: String, value: Int) {
        when (key) {
            "copyLeader" -> ModConfig.copyLeaderKey = value
            "leaveParty" -> ModConfig.leavePartyKey = value
        }
        ModConfig.save()
    }

    private fun keyName(code: Int): String = when (code) {
        -1 -> "NONE"
        -2 -> "M1"
        -3 -> "M2"
        -4 -> "M3"
        256 -> "ESC"
        else -> org.lwjgl.glfw.GLFW.glfwGetKeyName(code, 0)?.uppercase() ?: "KEY$code"
    }

    private fun getBtnY(feature: FeatureButton): Int {
        var y = COLUMN_START_Y + COLUMN_HEADER_HEIGHT + BUTTON_PADDING
        val colFeatures = features.filter { it.column == feature.column }.sortedBy { it.row }
        for (f in colFeatures) {
            if (f.row == feature.row) break
            y += BUTTON_HEIGHT + BUTTON_PADDING
            if (openDropdown == f) {
                y += f.dropdownOptions.size * (BUTTON_HEIGHT + BUTTON_PADDING)
            }
        }
        return y
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        // Draw standard darkened background
        guiGraphics.fill(0, 0, width, height, 0x90000000.toInt())

        hoveredButton = null
        hoveredDropdownIndex = -1

        val columns = features.groupBy { it.column }
        columns.forEach { (colIndex, colFeatures) ->
            val x = COLUMN_START_X + colIndex * (COLUMN_WIDTH + 10)
            val colName = if (colIndex == 0) "General" else if (colIndex == 1) "Security" else "Misc"

            guiGraphics.fill(x, COLUMN_START_Y, x + COLUMN_WIDTH, COLUMN_START_Y + COLUMN_HEADER_HEIGHT, HEADER_COLOR)
            renderOutline(guiGraphics, x, COLUMN_START_Y, COLUMN_WIDTH, COLUMN_HEADER_HEIGHT, BORDER_COLOR)
            drawCenteredText(guiGraphics, colName, x + COLUMN_WIDTH / 2, COLUMN_START_Y + 7, TEXT_COLOR)

            colFeatures.sortedBy { it.row }.forEach { feature ->
                val btnY = getBtnY(feature)
                val isHovered = mouseX in x..(x + COLUMN_WIDTH) && mouseY in btnY..(btnY + BUTTON_HEIGHT)
                if (isHovered) hoveredButton = feature

                val btnColor = if (feature.isEnabled()) ENABLED_COLOR else DISABLED_COLOR
                guiGraphics.fill(x, btnY, x + COLUMN_WIDTH, btnY + BUTTON_HEIGHT, btnColor)
                renderOutline(guiGraphics, x, btnY, COLUMN_WIDTH, BUTTON_HEIGHT, BORDER_COLOR)
                drawCenteredText(guiGraphics, feature.label, x + COLUMN_WIDTH / 2, btnY + 6, TEXT_COLOR)

                if (openDropdown == feature && feature.dropdownOptions.isNotEmpty()) {
                    feature.dropdownOptions.forEachIndexed { i, option ->
                        val dropY = btnY + BUTTON_HEIGHT + BUTTON_PADDING + i * (BUTTON_HEIGHT + BUTTON_PADDING)
                        val isDropHovered = mouseX in x..(x + COLUMN_WIDTH) && mouseY in dropY..(dropY + BUTTON_HEIGHT)
                        if (isDropHovered) {
                            hoveredDropdownIndex = i
                            hoveredButton = null
                        }

                        val dropColor = when {
                            isDropHovered -> DROPDOWN_HOVER_COLOR
                            option.isEnabled() -> DROPDOWN_ENABLED_COLOR
                            else -> DROPDOWN_COLOR
                        }
                        guiGraphics.fill(x, dropY, x + COLUMN_WIDTH, dropY + BUTTON_HEIGHT, dropColor)
                        renderOutline(guiGraphics, x, dropY, COLUMN_WIDTH, BUTTON_HEIGHT, BORDER_COLOR)
                        drawCenteredText(guiGraphics, option.label, x + COLUMN_WIDTH / 2, dropY + 6, TEXT_COLOR)

                        if (option.keybindKey != null) {
                            val keyBtnX = x + COLUMN_WIDTH + 4
                            val keyBtnW = 60
                            val isBinding = bindingFeature == option.keybindKey
                            val keyBtnColor = if (isBinding) KEYBIND_BINDING_COLOR else KEYBIND_COLOR
                            guiGraphics.fill(keyBtnX, dropY, keyBtnX + keyBtnW, dropY + BUTTON_HEIGHT, keyBtnColor)
                            renderOutline(guiGraphics, keyBtnX, dropY, keyBtnW, BUTTON_HEIGHT, BORDER_COLOR)
                            val keyLabel = if (isBinding) "..." else keyName(getKeybindValue(option.keybindKey))
                            drawCenteredText(guiGraphics, keyLabel, keyBtnX + keyBtnW / 2, dropY + 6, TEXT_COLOR)
                        }
                    }
                }
            }
        }

        if (bindingMode) {
            val msg = "Press a key to bind (ESC to cancel)"
            val tw = minecraft!!.font.width(msg)
            guiGraphics.fill(width / 2 - tw / 2 - 6, height - 24, width / 2 + tw / 2 + 6, height - 8, 0xCC000000.toInt())
            drawCenteredText(guiGraphics, msg, width / 2, height - 20, TEXT_COLOR)
        }

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        val mouseX = event.x.toInt()
        val mouseY = event.y.toInt()

        if (bindingMode) {
            val code = -(event.button() + 2)
            if (bindingFeature != null) setKeybindValue(bindingFeature!!, code)
            bindingMode = false
            bindingFeature = null
            return true
        }

        openDropdown?.let { feature ->
            val x = COLUMN_START_X + feature.column * (COLUMN_WIDTH + 10)
            val featureBtnY = getBtnY(feature)
            feature.dropdownOptions.forEachIndexed { i, option ->
                if (option.keybindKey != null) {
                    val dropY = featureBtnY + BUTTON_HEIGHT + BUTTON_PADDING + i * (BUTTON_HEIGHT + BUTTON_PADDING)
                    val keyBtnX = x + COLUMN_WIDTH + 4
                    val keyBtnW = 60
                    if (mouseX in keyBtnX..(keyBtnX + keyBtnW) && mouseY in dropY..(dropY + BUTTON_HEIGHT)) {
                        bindingFeature = option.keybindKey
                        bindingMode = true
                        return true
                    }
                }
            }
        }

        if (openDropdown != null && hoveredButton != openDropdown && hoveredDropdownIndex == -1) {
            openDropdown = null
            return true
        }

        if (openDropdown != null && hoveredDropdownIndex >= 0) {
            openDropdown!!.dropdownOptions[hoveredDropdownIndex].toggle()
            return true
        }

        hoveredButton?.let { feature ->
            when (event.button()) {
                0 -> feature.toggle()
                1 -> openDropdown = if (openDropdown == feature) null else feature
            }
            return true
        }

        openDropdown = null
        return super.mouseClicked(event, bl)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (bindingMode) {
            if (event.key != 256) {
                if (bindingFeature != null) setKeybindValue(bindingFeature!!, event.key)
            }
            bindingMode = false
            bindingFeature = null
            return true
        }
        if (event.key == 256) {
            onClose()
            return true
        }
        return super.keyPressed(event)
    }

    override fun isPauseScreen() = false

    private fun drawCenteredText(guiGraphics: GuiGraphics, text: String, x: Int, y: Int, color: Int) {
        val textWidth = minecraft!!.font.width(text)
        // No PoseStack, No matrix errors, No unresolved references.
        guiGraphics.drawString(minecraft!!.font, text, x - textWidth / 2, y, color, false)
    }
}