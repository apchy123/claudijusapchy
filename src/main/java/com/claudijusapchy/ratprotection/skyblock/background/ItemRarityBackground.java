package com.claudijusapchy.ratprotection.skyblock.background;

import com.claudijusapchy.ratprotection.config.ModConfig;
import com.claudijusapchy.ratprotection.skyblock.SkyblockItemRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRarityBackground extends ColoredItemBackground<SkyblockItemRarity> {

    @Override
    public boolean isEnabled() {
        return ModConfig.INSTANCE.getItemRarityBackgrounds();
    }

    @Override
    protected SkyblockItemRarity getColorKey(ItemStack stack) {
        if (stack.isEmpty()) return null;

        SkyblockItemRarity rarity = stack.getOrDefault(net.minecraft.core.component.DataComponents.LORE, net.minecraft.world.item.component.ItemLore.EMPTY)
                .styledLines()
                .reversed()
                .stream()
                .map(net.minecraft.network.chat.Component::getString)
                .map(SkyblockItemRarity::containsName)
                .flatMap(java.util.Optional::stream)
                .findFirst()
                .orElse(SkyblockItemRarity.UNKNOWN);

        return rarity == SkyblockItemRarity.UNKNOWN ? null : rarity;
    }

    @Override
    protected void draw(GuiGraphics graphics, int x, int y, SkyblockItemRarity rarity) {
        float opacity = ModConfig.INSTANCE.getItemBackgroundOpacity();
        int color = ARGB.colorFromFloat(opacity, rarity.r, rarity.g, rarity.b);
        ResourceLocation texId = ModConfig.INSTANCE.getItemBackgroundStyleSquare()
                ? ResourceLocation.fromNamespaceAndPath("ratprotection", "item_background_square")
                : ResourceLocation.fromNamespaceAndPath("ratprotection", "item_background_circular");
        graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texId, x, y, 16, 16, color);
    }
    }
