package com.claudijusapchy.ratprotection.mixin;

//#
import com.claudijusapchy.ratprotection.features.ZoomFeature;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class ScrollMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (ZoomFeature.isZooming) {
            ZoomFeature.INSTANCE.onScroll(yOffset);
            ci.cancel(); // prevent inventory/chat scrolling while zoomed
        }
    }
}
