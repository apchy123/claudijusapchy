package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.events.impl.ChatReceivedEvent
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import java.util.regex.Pattern

object PartyJoinSoundFeature : Feature(
    id = "partyJoinSound",
    name = "Party Join Sound",
    enabled = { PartyJoinSoundFeature.partyJoinSoundEnabled }
) {

    @JvmField
    var partyJoinSoundEnabled = true

    private val mc get() = Minecraft.getInstance()
    private val PARTY_JOIN_PATTERN = Pattern.compile("^\\w+ joined the party[.!]?$")

    override fun onActivate() {
        subscribe(ChatReceivedEvent::class.java, ::onChat)
    }

    private fun onChat(event: ChatReceivedEvent) {
        val message = event.strippedMessage
        if (!isPartyJoinMessage(message)) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        level.playSound(
            player,
            player.blockPosition(),
            SoundEvents.NOTE_BLOCK_PLING.value(),
            SoundSource.PLAYERS,
            2.0f,
            1.0f
        )
    }

    fun isPartyJoinMessage(message: String): Boolean {
        if (message.contains("joined the party")) return true
        if (message.contains("joined the dungeon group")) return true
        if (message.contains("has invited you to join their party")) return true
        return PARTY_JOIN_PATTERN.matcher(message).matches()
    }
}