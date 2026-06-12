package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.skyblock.background.ItemBackgroundManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HotbarRenderMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void onRenderHotbarSlot(GuiGraphics graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        if (stack.isEmpty()) return;
        ItemBackgroundManager.drawBackgrounds(stack, graphics, x, y);
    }
}