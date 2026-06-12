package com.claudijusapchy.ratprotection.skyblock.background;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemBackgroundManager {

    private static final List<ColoredItemBackground<?>> BACKGROUNDS = List.of(
            new ItemRarityBackground()
    );

    public static void init() {
        final int[] tick = {0};
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tick[0]++;
            if (tick[0] >= 6000) {
                tick[0] = 0;
                BACKGROUNDS.forEach(ColoredItemBackground::clearCache);
            }
        });
    }

    public static void drawBackgrounds(ItemStack stack, GuiGraphics graphics, int x, int y) {
        if (!com.claudijusapchy.ratprotection.SkyblockUtils.INSTANCE.isOnSkyblock()) return;
        for (ColoredItemBackground<?> bg : BACKGROUNDS) {
            if (bg.isEnabled()) {
                bg.tryDraw(stack, graphics, x, y);
            }
        }
    }
}