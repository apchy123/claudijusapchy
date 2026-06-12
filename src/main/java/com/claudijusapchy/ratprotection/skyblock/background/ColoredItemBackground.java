package com.claudijusapchy.ratprotection.skyblock.background;


import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public abstract class ColoredItemBackground<T> {

    private final HashMap<ItemStack, T> cache = new HashMap<>();

    public abstract boolean isEnabled();
    protected abstract T getColorKey(ItemStack stack);
    protected abstract void draw(GuiGraphics graphics, int x, int y, T colorKey);

    public final void tryDraw(ItemStack stack, GuiGraphics graphics, int x, int y) {
        T key = cache.computeIfAbsent(stack, this::getColorKey);
        if (key != null) draw(graphics, x, y, key);
    }

    public final void clearCache() {
        cache.clear();
    }
}