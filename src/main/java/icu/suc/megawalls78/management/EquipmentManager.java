package icu.suc.megawalls78.management;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.util.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.inventory.CraftMetaShield;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EquipmentManager {

    public static final Map<String, Pattern> PATTERNS = Map.of(
            "flower", new Pattern(DyeColor.BLACK, PatternType.FLOWER),
            "creeper", new Pattern(DyeColor.BLACK, PatternType.CREEPER),
            "skull", new Pattern(DyeColor.BLACK, PatternType.SKULL),
            "mojang", new Pattern(DyeColor.BLACK, PatternType.MOJANG),
            "globe", new Pattern(DyeColor.BLACK, PatternType.GLOBE),
            "piglin", new Pattern(DyeColor.BLACK, PatternType.PIGLIN),
            "flow", new Pattern(DyeColor.BLACK, PatternType.FLOW),
            "guster", new Pattern(DyeColor.BLACK, PatternType.GUSTER)
    );
    public static final Map<String, TrimPattern> TRIMS = Maps.newHashMap();

    private final Map<UUID, Pattern> patternCache;
    private final Map<UUID, Boolean> patternCached;
    private final Map<UUID, TrimPattern> trimCache;

    public EquipmentManager() {
        this.patternCache = Maps.newHashMap();
        this.patternCached = Maps.newHashMap();
        this.trimCache = Maps.newHashMap();
    }

    public boolean isPatternCached(UUID player) {
        return patternCached.getOrDefault(player, false);
    }

    public Pattern getPattern(UUID player) {
        if (isPatternCached(player)) {
            return patternCache.get(player);
        }
        CompletableFuture<String> future = MegaWalls78.getInstance().getDatabaseManager().getPlayerPattern(player);
        try {
            String s = future.get();
            if (s == null) {
                patternCache.put(player, null);
                patternCached.put(player, true);
                return null;
            }
            Pattern pattern = PATTERNS.get(s);
            patternCache.put(player, pattern);
            patternCached.put(player, true);
            return pattern;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPattern(UUID player, Pattern pattern) {
        patternCache.put(player, pattern);
        patternCached.put(player, true);
        MegaWalls78.getInstance().getDatabaseManager().setPlayerPattern(player, pattern);
    }

    public TrimPattern getTrim(UUID player) {
        TrimPattern trim = trimCache.get(player);
        if (trim == null) {
            try {
                String s = MegaWalls78.getInstance().getDatabaseManager().getPlayerTrim(player).get();
                if (s == null) {
                    trim = TrimPattern.SHAPER;
                } else {
                    trim = TRIMS.get(s);
                    if (trim == null) {
                        trim = TrimPattern.SHAPER;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        trimCache.put(player, trim);
        return trim;
    }

    public void setTrim(UUID player, TrimPattern trim) {
        trimCache.put(player, trim);
        MegaWalls78.getInstance().getDatabaseManager().setPlayerTrim(player, trim);
    }

    public void clearCache(UUID player) {
        trimCache.remove(player);
        patternCache.remove(player);
        patternCached.remove(player);
    }

    public static void decorate(ItemStack itemStack, GamePlayer gamePlayer) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        if (itemStack.getType().equals(Material.SHIELD)) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            CraftMetaShield shieldMeta = (CraftMetaShield) itemMeta;
            for (int i = 0; i < shieldMeta.numberOfPatterns(); i++) {
                shieldMeta.removePattern(i);
            }
            shieldMeta.addPattern(Color.getPattern(gamePlayer.getTeam().color()));
            Pattern pattern = MegaWalls78.getInstance().getEquipmentManager().getPattern(gamePlayer.getUuid());
            if (pattern != null) {
                shieldMeta.addPattern(pattern);
            }
            itemStack.setItemMeta(shieldMeta);
        } else if (itemMeta instanceof ArmorMeta armorMeta) {
            TrimMaterial color = Color.getTrim(gamePlayer.getTeam().color());
            if (color == null) {
                return;
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            armorMeta.setTrim(new ArmorTrim(color, MegaWalls78.getInstance().getEquipmentManager().getTrim(gamePlayer.getUuid())));
            itemStack.setItemMeta(armorMeta);
        }
    }

//    public static void clear(ItemStack itemStack) {
//        if (itemStack == null || itemStack.isEmpty()) {
//            return;
//        }
//        ItemMeta itemMeta = itemStack.getItemMeta();
//        if (itemMeta == null) {
//            return;
//        }
//        if (itemStack.getType().equals(Material.SHIELD)) {
//            itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
//            CraftMetaShield shieldMeta = (CraftMetaShield) itemMeta;
//            for (int i = 0; i < shieldMeta.numberOfPatterns(); i++) {
//                shieldMeta.removePattern(i);
//            }
//            shieldMeta.setBaseColor(null);
//            itemStack.setItemMeta(shieldMeta);
//        } else if (itemMeta instanceof ArmorMeta armorMeta) {
//            itemMeta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
//            armorMeta.setTrim(null);
//            itemStack.setItemMeta(armorMeta);
//        }
//    }

    static {
        TRIMS.put("sentry", TrimPattern.SENTRY);
        TRIMS.put("vex", TrimPattern.VEX);
        TRIMS.put("wild", TrimPattern.WILD);
        TRIMS.put("coast", TrimPattern.COAST);
        TRIMS.put("dune", TrimPattern.DUNE);
        TRIMS.put("wayfinder", TrimPattern.WAYFINDER);
        TRIMS.put("raiser", TrimPattern.RAISER);
        TRIMS.put("shaper", TrimPattern.SHAPER);
        TRIMS.put("host", TrimPattern.HOST);
        TRIMS.put("ward", TrimPattern.WARD);
        TRIMS.put("silence", TrimPattern.SILENCE);
        TRIMS.put("tide", TrimPattern.TIDE);
        TRIMS.put("snout", TrimPattern.SNOUT);
        TRIMS.put("rib", TrimPattern.RIB);
        TRIMS.put("eye", TrimPattern.EYE);
        TRIMS.put("spire", TrimPattern.SPIRE);
        TRIMS.put("flow", TrimPattern.FLOW);
        TRIMS.put("bolt", TrimPattern.BOLT);
    }
}
