package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.ModLogger
import com.claudijusapchy.ratprotection.config.ModConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

object PartyFinderRightClick {
    fun isKeyDown(window: Long, key: Int): Boolean {
        return if (key < -1) {
            GLFW.glfwGetMouseButton(window, -(key + 2)) == GLFW.GLFW_PRESS
        } else {
            GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
        }
    }
    var enabled = true
    var copyLeaderEnabled = true
    var leavePartyEnabled = true
    private val nameRegex = Regex("^(\\w{1,16})'s Party$")
    private var lastCopyTime = 0L  // ADD THIS

    fun init() {}

    fun doCopyLeader(mc: Minecraft, itemStack: ItemStack): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastCopyTime < 500) return false  // ADD THIS — ignore if fired within 500ms
        lastCopyTime = now  // ADD THIS

        val name = itemStack.customName?.string ?: run {
            ModLogger.warn("[RatProtection] Could not get party name.")
            return false
        }
        val match = nameRegex.matchEntire(name)?.groupValues?.drop(1) ?: run {
            ModLogger.warn("[RatProtection] Regex did not match: $name")
            return false
        }
        val leader = match.firstOrNull() ?: return false
        mc.keyboardHandler.clipboard = leader
        ModLogger.success("[RatProtection] Copied leader: §a$leader")
        return true
    }

    fun doLeaveParty(mc: Minecraft): Boolean {
        mc.player?.connection?.sendCommand("p leave")
        ModLogger.info("[RatProtection] Left party.")
        return true
    }

    fun doCopyLeaderFromSlot(mc: Minecraft, slot: Slot) {
        if (!enabled || !copyLeaderEnabled) return
        if (slot.container == mc.player?.inventory) return
        if (slot.index >= 45) return
        if (slot.item.item != Items.PLAYER_HEAD) return
        doCopyLeader(mc, slot.item)
    }

    fun doLeavePartyFromSlot(mc: Minecraft, slot: Slot) {
        if (!enabled || !leavePartyEnabled) return
        if (slot.container == mc.player?.inventory) return
        if (slot.index >= 45) return
        if (slot.item.item != Items.PLAYER_HEAD) return
        doLeaveParty(mc)
    }

    fun onSlotRightClicked(screen: AbstractContainerScreen<*>, slot: Slot): Boolean {
        if (!enabled) return false
        if (screen.title.string != "Party Finder") return false

        val mc = Minecraft.getInstance()
        if (slot.container == mc.player?.inventory) return false
        if (slot.index >= 45) return false
        if (slot.item.item != Items.PLAYER_HEAD) return false

        val window = GLFW.glfwGetCurrentContext()
        val copyHeld = ModConfig.copyLeaderKey != -1 && isKeyDown(window, ModConfig.copyLeaderKey)
        val leaveHeld = ModConfig.leavePartyKey != -1 && isKeyDown(window, ModConfig.leavePartyKey)

        return when {
            copyHeld && copyLeaderEnabled -> doCopyLeader(mc, slot.item)
            leaveHeld && leavePartyEnabled -> doLeaveParty(mc)
            !copyHeld && !leaveHeld && copyLeaderEnabled -> doCopyLeader(mc, slot.item)
            !copyHeld && !leaveHeld && leavePartyEnabled -> doLeaveParty(mc)
            else -> false
        }
    }
}