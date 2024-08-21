package icu.suc.megawalls78.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.custom.GrapplingHook;
import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.event.GrapplingHookEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.EquipmentManager;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.*;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntFloatMutablePair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.entity.CraftFishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameListener implements Listener {

    public static final PotionEffect COW_MILK_RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false);
    public static final PotionEffect COW_MILK_REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false);
    public static final int MOLEMAN_COOKIE_ENERGY = 3;
    public static final int MOLEMAN_PIE_ENERGY = 12;
    private static final PotionEffect MOLEMAN_JUNK_APPLE_REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false);

    private static final JoinConfiguration ACBE_JOIN = JoinConfiguration.builder().separator(Component.space()).build();
    private static final JoinConfiguration ACTIONBAR_JOIN = JoinConfiguration.builder().separator(Component.text("   ")).build();

    private static final Map<UUID, Pair<Integer, Float>> ENERGY_BLINK = Maps.newHashMap();

    private static final double BEFORE_PBB = 0.1D;
    private static final double AFTER_PBB = 0.005D;
    private static final LootTable NEWBEE_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "newbee"));
    private static final LootTable NORMAL_CHEST = Bukkit.getLootTable(new NamespacedKey("mw78", "normal"));
    private static final Set<UUID> NO_NEWBEE = Sets.newHashSet();

    private static final Set<Material> DISABLE_ITEMS = Set.of(Material.BUCKET, Material.GLASS_BOTTLE, Material.WATER_BUCKET, Material.LAVA_BUCKET);

    @EventHandler
    public void onEnergyChange(EnergyChangeEvent event) {
        Player player = event.getPlayer();
        float energy = event.getEnergy();
        player.setLevel((int) energy);
        float max = event.getMax();
        player.setExp(energy / max);
        if (energy == max) {
            ENERGY_BLINK.put(player.getUniqueId(), IntFloatMutablePair.of(0, max));
        } else {
            ENERGY_BLINK.remove(player.getUniqueId());
            player.setExp(energy / max);
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
                    GamePlayer gamePlayer = gameManager.getPlayer(player);
                    updateCompass(gameManager, gamePlayer, player);
                    blinkEnergy(player);
                    processItems(player, gamePlayer);
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
            case ItemUtil.MOLEMAN_JUNK_APPLE -> {
                if (gamePlayer.getIdentity().equals(Identity.MOLEMAN)) {
                    player.addPotionEffect(MOLEMAN_JUNK_APPLE_REGENERATION);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING)) {
            for (Item item : event.getItems()) {
                HashMap<Integer, ItemStack> over = player.getInventory().addItem(item.getItemStack());
                if (over.isEmpty()) {
                    item.remove();
                } else {
                    item.setItemStack(over.get(0));
                }
            }
        }


        if (BlockUtil.isNatural(event.getBlockState().getType())) {
            Block block = event.getBlock();

            ChestRollEvent.Pre pre = new ChestRollEvent.Pre(player, event.getBlockState(), MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING) ? BEFORE_PBB : AFTER_PBB);
            Bukkit.getPluginManager().callEvent(pre);
            if (pre.isCancelled() || RandomUtil.RANDOM.nextDouble() > pre.getChance()) {
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
                lootTable(NORMAL_CHEST, inventory, block);
//                NORMAL_CHEST.fillInventory(inventory, RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
            } else {
                lootTable(NEWBEE_CHEST, inventory, block);
//                NEWBEE_CHEST.fillInventory(inventory, RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build());
                NO_NEWBEE.add(player.getUniqueId());
            }

            ChestRollEvent.Post post = new ChestRollEvent.Post(player, event.getBlockState(), inventory);
            Bukkit.getPluginManager().callEvent(post);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleGrapplingHook(PlayerFishEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = player.getEquipment().getItem(hand);
        if (ItemUtil.isMW78Item(itemStack, ItemUtil.GRAPPLING_HOOK)) {
            switch (event.getState()) {
                case FISHING -> {
                    Double max = ItemUtil.getMW78Tag(itemStack, ItemUtil.GRAPPLING_MAX, PersistentDataType.DOUBLE);
                    if (max == null) {
                        event.setCancelled(true);
                        return;
                    }

                    GrapplingHookEvent grapplingHookEvent = new GrapplingHookEvent(player, null, GrapplingHookEvent.State.CAST, itemStack);
                    Bukkit.getPluginManager().callEvent(grapplingHookEvent);
                    if (grapplingHookEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (itemStack.getItemMeta() instanceof Damageable damageable) {
                        event.getHook().remove();
                        double maxDamage = damageable.getMaxDamage();
                        max = (maxDamage - damageable.getDamage()) / maxDamage * max;
                    }
                    EntityUtil.spawn(player.getEyeLocation(), EntityUtil.Type.GRAPPLING_HOOK, null, player, max);
                }
                case CAUGHT_ENTITY -> {
                    if (event.getHook() instanceof CraftFishHook hook && hook.getHandle() instanceof GrapplingHook gh && gh.inGround()) {

                        GrapplingHookEvent grapplingHookEvent = new GrapplingHookEvent(player, hook, GrapplingHookEvent.State.PULL, itemStack);
                        Bukkit.getPluginManager().callEvent(grapplingHookEvent);
                        if (grapplingHookEvent.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }

                        player.setVelocity(EntityUtil.getPullVector(player, hook, 4.0D, 8.0D, 4.0D, true));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleGrapplingHook(PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();
        if (ItemUtil.isMW78Item(itemStack, ItemUtil.GRAPPLING_HOOK)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.isUnbreakable()) {
                return;
            }
            if (itemMeta instanceof Damageable damageable) {
                int damage = damageable.getMaxDamage() / 2;
                if (damageable.getDamage() >= damage) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(damage);
            }
        }
    }

    private void blinkEnergy(Player player) {
        Pair<Integer, Float> pair = ENERGY_BLINK.get(player.getUniqueId());
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
    }

    private void updateCompass(GameManager gameManager, GamePlayer gamePlayer, Player player) {
        if (gamePlayer != null) {
            double nearest = Double.MAX_VALUE;
            Location location = player.getLocation();
            GameTeam tracking = gamePlayer.getTracking();
            Set<GamePlayer> targets = gameManager.getTeamPlayersMap().get(tracking);
            for (GamePlayer target : targets) {
                if (target.getUuid().equals(gamePlayer.getUuid())) {
                    continue;
                }
                Player targetBukkitPlayer = target.getBukkitPlayer();
                if (targetBukkitPlayer == null || gameManager.isSpectator(targetBukkitPlayer)) {
                    continue;
                }
                Location targetBukkitPlayerLocation = targetBukkitPlayer.getLocation();
                double distance = player.getLocation().distance(targetBukkitPlayerLocation);
                if (distance < nearest) {
                    nearest = distance;
                    location = targetBukkitPlayerLocation;
                }
            }
            player.setCompassTarget(location);
            if (ItemUtil.isMW78Item(PlayerUtil.getPlayerMainHand(player), ItemUtil.COMPASS)) {
                List<ComponentLike> components = Lists.newArrayList();
                components.add(Component.translatable("mw78.compass.tracking", tracking.name().color(tracking.color())));
                Component distance;
                if (nearest == Double.MAX_VALUE) {
                    distance = Component.translatable("mw78.compass.null");
                } else {
                    Component dir;
                    if (location.getY() > player.getY()) {
                        dir = Component.translatable("mw78.compass.up");
                    } else if (location.getY() < player.getY()) {
                        dir = Component.translatable("mw78.compass.down");
                    } else {
                        dir = Component.translatable("mw78.compass.equal");
                    }
                    distance = Component.translatable("mw78.compass.distance", Component.text(Formatters.COMPASS.format(nearest)), dir.color(NamedTextColor.WHITE));
                }
                components.add(Component.translatable("mw78.compass.nearest", distance.color(tracking.color())));
                player.sendActionBar(Component.join(ACTIONBAR_JOIN, components));
            } else {
                List<ComponentLike> actionbar = gamePlayer.getActionbar();
                List<ComponentLike> components = Lists.newArrayList();
                for (int i = 0; i < actionbar.size(); i++) {
                    components.add(Component.join(ACBE_JOIN, actionbar.get(i), actionbar.get(++i)));
                }
                player.sendActionBar(Component.join(ACTIONBAR_JOIN, components));
            }
        }
    }

    private void processItems(Player player, GamePlayer gamePlayer) {
        PlayerInventory inventory = player.getInventory();
        processItems(inventory, inventory.getStorageContents(), gamePlayer);
        processItems(inventory, inventory.getArmorContents(), gamePlayer);
        processItems(inventory, inventory.getExtraContents(), gamePlayer);
    }

    private void processItems(PlayerInventory inventory, ItemStack[] items, GamePlayer gamePlayer) {
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (itemStack == null) {
                continue;
            }
            Material type = itemStack.getType();
            if (DISABLE_ITEMS.contains(type)) {
                inventory.clear(i);
                continue;
            }
            EquipmentManager.decorate(itemStack, gamePlayer);
        }
    }

    private void lootTable(LootTable lootTable, Inventory inventory, Block block) {
        if (lootTable == null) {
            return;
        }
        int size = inventory.getSize();
        for (ItemStack itemStack : lootTable.populateLoot(RandomUtil.RANDOM, new LootContext.Builder(block.getLocation()).build())) {
            for (int i = 0; i < 3; i++) {
                int slot = RandomUtil.RANDOM.nextInt(size);
                if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, itemStack);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item item && DISABLE_ITEMS.contains(item.getItemStack().getType())) {
            event.setCancelled(true);
        }
    }
}
