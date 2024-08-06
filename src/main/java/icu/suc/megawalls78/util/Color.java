package icu.suc.megawalls78.util;

import com.google.common.collect.Maps;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.material.Dye;

import java.util.Map;

public class Color {

    private static final Map<NamedTextColor, DyeColor> NAMED_TEXT_COLOR_DYE_COLOR_MAP = Maps.newHashMap();

    public static DyeColor fromNamedTextColor(NamedTextColor textColor) {
        return NAMED_TEXT_COLOR_DYE_COLOR_MAP.get(textColor);
    }

    static {
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.WHITE, DyeColor.WHITE);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.GOLD, DyeColor.ORANGE);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.AQUA, DyeColor.LIGHT_BLUE);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.YELLOW, DyeColor.YELLOW);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.GREEN, DyeColor.LIME);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.LIGHT_PURPLE, DyeColor.PINK);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.DARK_GRAY, DyeColor.GRAY);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.GRAY, DyeColor.LIGHT_GRAY);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.DARK_AQUA, DyeColor.CYAN);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.DARK_PURPLE, DyeColor.PURPLE);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.BLUE, DyeColor.BLUE);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.DARK_GREEN, DyeColor.GREEN);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.RED, DyeColor.RED);
        NAMED_TEXT_COLOR_DYE_COLOR_MAP.put(NamedTextColor.BLACK, DyeColor.BLACK);
    }
}
