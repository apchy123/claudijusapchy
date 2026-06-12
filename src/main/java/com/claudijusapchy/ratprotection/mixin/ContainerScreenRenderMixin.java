package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.skyblock.background.ItemBackgroundManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenRenderMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void onRenderSlot(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (!slot.hasItem()) return;
        ItemBackgroundManager.drawBackgrounds(slot.getItem(), graphics, slot.x, slot.y);
    }
}