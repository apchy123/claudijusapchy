package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.features.PartyFinderRightClick;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, priority = 1001)
public abstract class ContainerScreenMixin {

    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Inject(
            method = "slotClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSlotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        if (clickType != ClickType.PICKUP) return;
        if (j != 1) return; // j == 1 means right click
        if (slot == null) return;

        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        boolean cancelled = PartyFinderRightClick.INSTANCE.onSlotRightClicked(screen, slot);
        if (cancelled) ci.cancel();
    }
}