package icu.suc.megawalls78.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.BlockUtil;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ItemUtil;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SkillListener implements Listener {

    public static final PotionEffect COW_MILK_RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false);
    public static final PotionEffect COW_MILK_REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false);
    public static final int MOLEMAN_COOKIE_ENERGY = 3;
    public static final int MOLEMAN_PIE_ENERGY = 12;

    private static final JoinConfiguration ACBE_JOIN = JoinConfiguration.builder().separator(Component.space()).build();
    private static final JoinConfiguration ACTIONBAR_JOIN = JoinConfiguration.builder().separator(Component.text("   ")).build();

    private static final Map<UUID, Pair<Integer, Integer>> ENERGY_BLINK = Maps.newHashMap();

    private static final double BEFORE_PBB = 0.1D;
    private static final double AFTER_PBB = 0.01D;
    private static final LootTable NEWBEE_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "newbee"));
    private static final LootTable NORMAL_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "normal"));
    private static final Set<UUID> NO_NEWBEE = Sets.newHashSet();

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
        GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(player);
        String mw78Id = ItemUtil.getMW78Id(event.getItem());
        if (mw78Id == null) {
            return;
        }
        switch (mw78Id) {
            case ItemUtil.COW_MILK -> {
                player.addPotionEffect(COW_MILK_RESISTANCE);
                player.addPotionEffect(COW_MILK_REGENERATION);
            }
            case ItemUtil.MOLEMAN_COOKIE -> gamePlayer.increaseEnergy(MOLEMAN_COOKIE_ENERGY);
            case ItemUtil.MOLEMAN_PIE -> gamePlayer.increaseEnergy(MOLEMAN_PIE_ENERGY);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        List<Item> items = event.getItems();
        for (Item item : items) {
            HashMap<Integer, ItemStack> over = player.getInventory().addItem(item.getItemStack());
            if (over.isEmpty()) {
                item.remove();
            } else {
                item.setItemStack(over.get(0));
            }
        }

        if (BlockUtil.isNatural(event.getBlockState().getType())) {
            Block block = event.getBlock();

            ChestRollEvent.Pre pre = new ChestRollEvent.Pre(player, event.getBlockState(), MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING) ? BEFORE_PBB : AFTER_PBB);
            Bukkit.getPluginManager().callEvent(pre);
            if (pre.isCancelled() || RandomUtil.RANDOM.nextDouble() > pre.getProbability()) {
                return;
            }

            block.setType(Material.TRAPPED_CHEST);
            Chest chest = (Chest) block.getState();
            Directional data = (Directional) chest.getBlockData();
            data.setFacing(EntityUtil.getFacingTowards(block, player));
            chest.setBlockData(data);
            chest.update();
            Inventory inventory = chest.getBlockInventory();
            if (NO_NEWBEE.contains(player.getUniqueId())) {
                NORMAL_CHEST.fillInventory(inventory, RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
            } else {
                NEWBEE_CHEST.fillInventory(inventory, RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
                NO_NEWBEE.add(player.getUniqueId());
            }

            ChestRollEvent.Post post = new ChestRollEvent.Post(player, event.getBlockState(), inventory);
            Bukkit.getPluginManager().callEvent(post);
        }
    }
}
