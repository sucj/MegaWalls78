package icu.suc.megawalls78.util;

import com.google.common.collect.Maps;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.Map;

public class Color {

    private static final Map<NamedTextColor, DyeColor> DYE_COLOR = Maps.newHashMap();
    private static final Map<NamedTextColor, Pattern> PATTERN = Maps.newHashMap();
    private static final Map<NamedTextColor, TrimMaterial> TRIM = Maps.newHashMap();

    public static DyeColor getDye(NamedTextColor color) {
        return DYE_COLOR.get(color);
    }

    public static Pattern getPattern(NamedTextColor color) {
        Pattern pattern = PATTERN.get(color);
        if (pattern == null) {
            DyeColor dye = getDye(color);
            if (dye == null) {
                dye = DyeColor.WHITE;
            }
            pattern = new Pattern(dye, PatternType.BASE);
            PATTERN.put(color, pattern);
        }
        return pattern;
    }

    public static TrimMaterial getTrim(NamedTextColor color) {
        return TRIM.get(color);
    }

    static {
        DYE_COLOR.put(NamedTextColor.WHITE, DyeColor.WHITE);
        DYE_COLOR.put(NamedTextColor.GOLD, DyeColor.ORANGE);
        DYE_COLOR.put(NamedTextColor.AQUA, DyeColor.LIGHT_BLUE);
        DYE_COLOR.put(NamedTextColor.YELLOW, DyeColor.YELLOW);
        DYE_COLOR.put(NamedTextColor.GREEN, DyeColor.LIME);
        DYE_COLOR.put(NamedTextColor.LIGHT_PURPLE, DyeColor.PINK);
        DYE_COLOR.put(NamedTextColor.DARK_GRAY, DyeColor.GRAY);
        DYE_COLOR.put(NamedTextColor.GRAY, DyeColor.LIGHT_GRAY);
        DYE_COLOR.put(NamedTextColor.DARK_AQUA, DyeColor.CYAN);
        DYE_COLOR.put(NamedTextColor.DARK_PURPLE, DyeColor.PURPLE);
        DYE_COLOR.put(NamedTextColor.BLUE, DyeColor.BLUE);
        DYE_COLOR.put(NamedTextColor.DARK_GREEN, DyeColor.GREEN);
        DYE_COLOR.put(NamedTextColor.RED, DyeColor.RED);
        DYE_COLOR.put(NamedTextColor.BLACK, DyeColor.BLACK);

        TRIM.put(NamedTextColor.WHITE, TrimMaterial.QUARTZ);
        TRIM.put(NamedTextColor.GRAY, TrimMaterial.IRON);
        TRIM.put(NamedTextColor.BLACK, TrimMaterial.NETHERITE);
        TRIM.put(NamedTextColor.RED, TrimMaterial.REDSTONE);
        TRIM.put(NamedTextColor.YELLOW, TrimMaterial.GOLD);
        TRIM.put(NamedTextColor.GREEN, TrimMaterial.EMERALD);
        TRIM.put(NamedTextColor.AQUA, TrimMaterial.DIAMOND);
        TRIM.put(NamedTextColor.BLUE, TrimMaterial.LAPIS);
        TRIM.put(NamedTextColor.DARK_PURPLE, TrimMaterial.AMETHYST);
    }
}
