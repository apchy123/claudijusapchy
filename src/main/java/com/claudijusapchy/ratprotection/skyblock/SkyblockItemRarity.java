package com.claudijusapchy.ratprotection.skyblock;

import net.minecraft.ChatFormatting;

public enum SkyblockItemRarity {
    COMMON(ChatFormatting.WHITE),
    UNCOMMON(ChatFormatting.GREEN),
    RARE(ChatFormatting.BLUE),
    EPIC(ChatFormatting.DARK_PURPLE),
    LEGENDARY(ChatFormatting.GOLD),
    MYTHIC(ChatFormatting.LIGHT_PURPLE),
    DIVINE(ChatFormatting.AQUA),
    SPECIAL(ChatFormatting.RED),
    VERY_SPECIAL(ChatFormatting.RED),
    ULTIMATE(ChatFormatting.DARK_RED),
    ADMIN(ChatFormatting.DARK_RED),
    UNKNOWN(ChatFormatting.DARK_GRAY);

    public final ChatFormatting formatting;
    public final int color;
    public final float r, g, b;

    public static java.util.Optional<SkyblockItemRarity> containsName(String name) {
        // Find last because "UNCOMMON" contains "COMMON" and "VERY_SPECIAL" contains "SPECIAL"
        return java.util.Arrays.stream(values())
                .filter(rarity -> name.contains(rarity.toString()))
                .reduce((first, second) -> second);
    }

    SkyblockItemRarity(ChatFormatting formatting) {
        this.formatting = formatting;
        this.color = formatting.getColor() != null ? formatting.getColor() : 0xAAAAAA;
        this.r = ((color >> 16) & 0xFF) / 255f;
        this.g = ((color >> 8) & 0xFF) / 255f;
        this.b = (color & 0xFF) / 255f;
    }

}