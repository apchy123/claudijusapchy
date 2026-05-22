package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.SecretTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class ScaledHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSecretTracker(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!SecretTracker.enabled || !SecretTracker.visible) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        int foundN = SecretTracker.getFoundN();
        int totalN = SecretTracker.getTotalN();
        float scale = SecretTracker.scale;

        int color;
        if (foundN >= totalN * 0.75) color = 0xFF55FF55;
        else if (foundN >= totalN * 0.50) color = 0xFFFFFF55;
        else color = 0xFFAAAAAA;

        String text = foundN + "/" + totalN;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(SecretTracker.hudX, SecretTracker.hudY, 0);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.drawString(mc.font, text, 0, 0, color, true);
        guiGraphics.pose().popMatrix();
    }
}