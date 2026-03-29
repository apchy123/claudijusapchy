package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.TextShadowConfig;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public class TextShadowMixin {

    @Inject(
            method = "getShadowColor",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeShadow(Style style, int i, CallbackInfoReturnable<Integer> cir) {
        if (!TextShadowConfig.shadowEnabled) {
            cir.setReturnValue(0);
        }
    }
}