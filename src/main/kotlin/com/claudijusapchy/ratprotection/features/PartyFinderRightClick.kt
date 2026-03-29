package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.ModLogger
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

object PartyFinderRightClick {

    private var inPartyFinder = false
    var mode = 0
    private val nameRegex = Regex("^(\\w{1,16})'s Party$")

    fun init() {
        // Nothing needed here anymore — handled by mixin
    }

    fun onSlotRightClicked(screen: AbstractContainerScreen<*>, slot: Slot): Boolean {
        val title = screen.title.string
        if (title != "Party Finder") return false

        val mc = Minecraft.getInstance()
        if (slot.container == mc.player?.inventory) return false
        if (slot.index >= 45) return false
        if (slot.item.item != Items.PLAYER_HEAD) return false

        val itemStack = slot.item

        when (mode) {
            0 -> {
                val name = itemStack.customName?.string ?: run {
                    ModLogger.warn("[RatProtection] Could not get party name.")
                    return false
                }
                ModLogger.info("[RatProtection] Item name: $name")
                val match = nameRegex.matchEntire(name)?.groupValues?.drop(1) ?: run {
                    ModLogger.warn("[RatProtection] Regex did not match: $name")
                    return false
                }
                val leader = match.firstOrNull() ?: return false
                mc.keyboardHandler.clipboard = leader
                ModLogger.success("[RatProtection] Copied leader: §a$leader")
                return true // cancel the click
            }
            1 -> {
                mc.player?.connection?.sendCommand("p leave")
                ModLogger.info("[RatProtection] Left party.")
                return true // cancel the click
            }
        }
        return false
    }
}