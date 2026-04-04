package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.ZoomFeature;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class ZoomMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void modifyFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (ZoomFeature.isZooming) {
            float originalFov = cir.getReturnValue();
            cir.setReturnValue(originalFov * ZoomFeature.cachedFactor);
        }
    }
}