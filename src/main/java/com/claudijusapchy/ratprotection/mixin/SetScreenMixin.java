package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.DisableWorldLoadingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class SetScreenMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (!DisableWorldLoadingScreen.INSTANCE.getEnabled()) return;
        if (screen instanceof LevelLoadingScreen) {
            DisableWorldLoadingScreen.INSTANCE.onScreenOpen(screen);
            ci.cancel();
        }
    }
}