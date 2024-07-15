package icu.suc.megawalls78.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.impl.herebrine.gathering.TreasureHunter;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.ItemUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import icu.suc.megawalls78.util.RandomUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SkillListener implements Listener {

    public static final PotionEffect COW_MILK_RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false);
    public static final PotionEffect COW_MILK_REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false);

    private static final JoinConfiguration ACBE_JOIN = JoinConfiguration.builder().separator(Component.space()).build();
    private static final JoinConfiguration ACTIONBAR_JOIN = JoinConfiguration.builder().separator(Component.text("   ")).build();

    private static final Map<UUID, Pair<Integer, Integer>> ENERGY_BLINK = Maps.newHashMap();

    private static final double BEFORE_PBB = 0.1D;
    private static final double AFTER_PBB = 0.01D;
    private static final LootTable NEWBEE_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "newbee"));
    private static final LootTable NORMAL_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "normal"));
    private static final Set<UUID> NO_NEWBEE = Sets.newHashSet();
    private static final Set<Material> GEN_CHEST = Sets.immutableEnumSet(Material.STONE, Material.DEEPSLATE,
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG);

    @EventHandler
    public void onEnergyChange(EnergyChangeEvent event) {
        Player player = event.getPlayer();
        int energy = event.getEnergy();
        player.setLevel(energy);
        int max = event.getMax();
        player.setExp((float) energy / max);
        if (energy == max) {
            ENERGY_BLINK.put(player.getUniqueId(), IntIntMutablePair.of(0, max));
        } else {
            ENERGY_BLINK.remove(player.getUniqueId());
            player.setExp((float) energy / max);
        }
    }

    @EventHandler
    public void onServerTick(ServerTickStartEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inFighting()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (gameManager.isSpectator(player)) {
                    ENERGY_BLINK.remove(player.getUniqueId());
                } else {
                    Pair<Integer, Integer> pair = ENERGY_BLINK.get(player.getUniqueId());
                    if (pair != null) {
                        Integer tick = pair.first();
                        switch (tick) {
                            case 0 -> {
                                player.setExp(1.0F);
                                pair.first(tick + 1);
                            }
                            case 10 -> {
                                player.setExp(0.0F);
                                pair.first(tick + 1);
                            }
                            case 20 -> pair.first(0);
                            default -> pair.first(tick + 1);
                        }
                    }
                    GamePlayer gamePlayer = gameManager.getPlayer(player);
                    if (gamePlayer != null) {
                        List<ComponentLike> actionbar = gamePlayer.getActionbar();
                        List<ComponentLike> components = Lists.newArrayList();
                        for (int i = 0; i < actionbar.size(); i++) {
                            components.add(Component.join(ACBE_JOIN, actionbar.get(i), actionbar.get(++i)));
                        }
                        player.sendActionBar(Component.join(ACTIONBAR_JOIN, components));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (ItemUtil.isCowMilk(event.getItem())) {
            player.addPotionEffect(COW_MILK_RESISTANCE);
            player.addPotionEffect(COW_MILK_REGENERATION);
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        for (Item item : event.getItems()) {
            player.getInventory().addItem(item.getItemStack());
        }
        if (GEN_CHEST.contains(event.getBlockState().getType()) && roll(player)) {
            Block block = event.getBlock();
            block.setType(Material.TRAPPED_CHEST);
            Chest chest = (Chest) block.getState();
            Directional data = (Directional) chest.getBlockData();
            data.setFacing(PlayerUtil.getFacingTowardsPlayer(block, player));
            chest.setBlockData(data);
            chest.update();
            if (NO_NEWBEE.contains(player.getUniqueId())) {
                NORMAL_CHEST.fillInventory(chest.getBlockInventory(), RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
            } else {
                NEWBEE_CHEST.fillInventory(chest.getBlockInventory(), RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
                NO_NEWBEE.add(player.getUniqueId());
            }
        }
    }

    private boolean roll(Player player) {
        double p = MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING) ? BEFORE_PBB : AFTER_PBB;
        if (MegaWalls78.getInstance().getGameManager().getPlayer(player).getGathering() instanceof TreasureHunter) {
            p *= 3;
        }
        return RandomUtil.RANDOM.nextDouble() < p;
    }
}
