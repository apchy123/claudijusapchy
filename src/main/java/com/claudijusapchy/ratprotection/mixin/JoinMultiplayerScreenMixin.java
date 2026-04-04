package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.SSIDScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    protected JoinMultiplayerScreenMixin(Component title) { super(title); }

    @Inject(at = @At("HEAD"), method = "init")
    private void onInit(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.literal("SSID"),
                        button -> Minecraft.getInstance().setScreen(SSIDScreen.getInstance()))
                .width(100).pos(20, 8).build());
    }
}