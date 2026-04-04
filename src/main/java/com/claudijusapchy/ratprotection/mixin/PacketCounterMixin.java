package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.LagTracker;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class PacketCounterMixin {
    private long lastCountedTick = -1;

    @Inject(method = "handleContainerSetSlot", at = @At("HEAD"))
    private void onSlotPacket(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        long now = System.currentTimeMillis() / 50;
        if (now != lastCountedTick) {
            lastCountedTick = now;
            LagTracker.INSTANCE.countPacket();
        }
    }
}