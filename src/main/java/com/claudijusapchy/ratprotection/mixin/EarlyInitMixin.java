package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.RatProtectionMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class EarlyInitMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void onEarlyInit(CallbackInfo ci) {
        RatProtectionMod.earlyInit();
    }
}