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

object ModScreen : Screen(Component.literal("Rat Protection")) {

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
        val action: () -> Unit
    )

    private val features = listOf(
        FeatureButton(
            label = "Text Shadow",
            isEnabled = { TextShadowConfig.shadowEnabled },
            toggle = {
                TextShadowConfig.shadowEnabled = !TextShadowConfig.shadowEnabled
                ModConfig.save()
            },
            column = 0,
            row = 0
        ),
        FeatureButton(
            label = "Party Finder",
            isEnabled = { true },
            toggle = {
                PartyFinderRightClick.mode = if (PartyFinderRightClick.mode == 0) 1 else 0
                ModConfig.save()
            },
            column = 0,
            row = 1,
            dropdownOptions = listOf(
                DropdownOption("Copy Leader") {
                    PartyFinderRightClick.mode = 0
                    ModConfig.save()
                },
                DropdownOption("Leave Party") {
                    PartyFinderRightClick.mode = 1
                    ModConfig.save()
                }
            )
        ),
        FeatureButton(
            label = "Rat Protection",
            isEnabled = { true },
            toggle = {},
            column = 0,
            row = 2
        )
    )

    private val COLUMN_WIDTH = 160
    private val BUTTON_HEIGHT = 20
    private val BUTTON_PADDING = 2
    private val COLUMN_HEADER_HEIGHT = 24
    private val COLUMN_START_X = 20
    private val COLUMN_START_Y = 20
    private val ENABLED_COLOR = Color(0, 120, 215).rgb
    private val DISABLED_COLOR = Color(60, 60, 60, 255).rgb
    private val HEADER_COLOR = Color(20, 20, 20, 240).rgb
    private val DROPDOWN_COLOR = Color(40, 40, 40, 255).rgb
    private val DROPDOWN_HOVER_COLOR = Color(0, 100, 180).rgb
    private val TEXT_COLOR = Color(255, 255, 255).rgb
    private val BORDER_COLOR = Color(80, 80, 80).rgb

    private var hoveredButton: FeatureButton? = null
    private var hoveredDropdownIndex: Int = -1
    private var openDropdown: FeatureButton? = null

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        guiGraphics.fill(0, 0, width, height, Color(0, 0, 0, 160).rgb)

        hoveredButton = null
        hoveredDropdownIndex = -1

        val columns = features.groupBy { it.column }

        columns.forEach { (colIndex, colFeatures) ->
            val x = COLUMN_START_X + colIndex * (COLUMN_WIDTH + 10)
            val colName = when (colIndex) {
                0 -> "General"
                1 -> "Security"
                else -> "Misc"
            }

            guiGraphics.fill(x, COLUMN_START_Y, x + COLUMN_WIDTH, COLUMN_START_Y + COLUMN_HEADER_HEIGHT, HEADER_COLOR)
            guiGraphics.renderOutline(x, COLUMN_START_Y, COLUMN_WIDTH, COLUMN_HEADER_HEIGHT, BORDER_COLOR)
            drawCenteredText(guiGraphics, colName, x + COLUMN_WIDTH / 2, COLUMN_START_Y + 7, TEXT_COLOR)

            colFeatures.forEach { feature ->
                val btnY = COLUMN_START_Y + COLUMN_HEADER_HEIGHT + BUTTON_PADDING + feature.row * (BUTTON_HEIGHT + BUTTON_PADDING)
                val isHovered = mouseX in x..(x + COLUMN_WIDTH) && mouseY in btnY..(btnY + BUTTON_HEIGHT)

                if (isHovered) hoveredButton = feature

                val btnColor = if (feature.isEnabled()) ENABLED_COLOR else DISABLED_COLOR
                guiGraphics.fill(x, btnY, x + COLUMN_WIDTH, btnY + BUTTON_HEIGHT, btnColor)
                guiGraphics.renderOutline(x, btnY, COLUMN_WIDTH, BUTTON_HEIGHT, BORDER_COLOR)

                val displayLabel = if (feature.label == "Party Finder") {
                    "Party Finder: ${if (PartyFinderRightClick.mode == 0) "Copy" else "Leave"}"
                } else feature.label

                drawCenteredText(guiGraphics, displayLabel, x + COLUMN_WIDTH / 2, btnY + 6, TEXT_COLOR)

                if (openDropdown == feature && feature.dropdownOptions.isNotEmpty()) {
                    feature.dropdownOptions.forEachIndexed { i, option ->
                        val dropY = btnY + BUTTON_HEIGHT + i * BUTTON_HEIGHT
                        val isDropHovered = mouseX in x..(x + COLUMN_WIDTH) && mouseY in dropY..(dropY + BUTTON_HEIGHT)
                        if (isDropHovered) hoveredDropdownIndex = i

                        val dropColor = if (isDropHovered) DROPDOWN_HOVER_COLOR else DROPDOWN_COLOR
                        guiGraphics.fill(x, dropY, x + COLUMN_WIDTH, dropY + BUTTON_HEIGHT, dropColor)
                        guiGraphics.renderOutline(x, dropY, COLUMN_WIDTH, BUTTON_HEIGHT, BORDER_COLOR)
                        drawCenteredText(guiGraphics, option.label, x + COLUMN_WIDTH / 2, dropY + 6, TEXT_COLOR)
                    }
                }
            }
        }

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        val mouseX = event.x.toInt()
        val mouseY = event.y.toInt()
        val button = event.button()

        if (openDropdown != null && hoveredButton != openDropdown && hoveredDropdownIndex == -1) {
            openDropdown = null
            return true
        }

        if (openDropdown != null && hoveredDropdownIndex >= 0) {
            openDropdown!!.dropdownOptions[hoveredDropdownIndex].action()
            openDropdown = null
            return true
        }

        hoveredButton?.let { feature ->
            when (button) {
                0 -> {
                    feature.toggle()
                    openDropdown = null
                }
                1 -> {
                    openDropdown = if (openDropdown == feature) null else feature
                }
            }
            return true
        }

        openDropdown = null
        return super.mouseClicked(event, bl)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (event.key == 256) {
            onClose()
            return true
        }
        return super.keyPressed(event)
    }

    override fun isPauseScreen() = false

    private fun drawCenteredText(guiGraphics: GuiGraphics, text: String, x: Int, y: Int, color: Int) {
        val textWidth = minecraft!!.font.width(text)
        guiGraphics.drawString(minecraft!!.font, text, x - textWidth / 2, y, color, false)
    }
}