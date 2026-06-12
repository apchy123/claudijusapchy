package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.SecretTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.DeltaTracker;

@Mixin(Gui.class)
public class ScaledHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSecretTracker(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!SecretTracker.enabled || !SecretTracker.visible) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        int foundN = SecretTracker.foundN;
        int totalN = SecretTracker.totalN;
        float scale = SecretTracker.scale;

        String found = String.valueOf(foundN);
        String slash = "/";
        String total = String.valueOf(totalN);

        int foundColor;
        if (foundN >= totalN * 0.75) foundColor = 0xFF55FF55;
        else if (foundN >= totalN * 0.50) foundColor = 0xFFFFFF55;
        else foundColor = 0xFFAAAAAA;

        int slashColor = 0xFFFFFFFF;
        int totalColor = 0xFF55FF55;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((float) SecretTracker.hudX, (float) SecretTracker.hudY);
        guiGraphics.pose().scale(scale, scale);

        int x = 0;
        guiGraphics.drawString(mc.font, found, x, 0, foundColor, true);
        x += mc.font.width(found);
        guiGraphics.drawString(mc.font, slash, x, 0, slashColor, true);
        x += mc.font.width(slash);
        guiGraphics.drawString(mc.font, total, x, 0, totalColor, true);

        guiGraphics.pose().popMatrix();
    }
}