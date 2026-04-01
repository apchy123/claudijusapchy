package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.LagTracker;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class PacketCounterMixin {
    @Inject(method = "genericsFtw", at = @At("HEAD"))
    private static void countPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        LagTracker.INSTANCE.countPacket();
    }
}