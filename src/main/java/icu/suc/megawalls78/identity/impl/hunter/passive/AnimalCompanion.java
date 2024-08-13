package icu.suc.megawalls78.identity.impl.hunter.passive;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Color;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class AnimalCompanion extends CooldownPassive {

    private static final double CHANCE = 0.2D;
    private static final int MAX = 3;

    private static final ItemBuilder ZOMBIE_PIGMAN_SWORD = ItemBuilder.of(Material.GOLDEN_SWORD);
    private static final ItemBuilder SKELETON_BOW = ItemBuilder.of(Material.BOW);

    private static final PotionEffect ZOMBIE_PIGMAN_POTION = new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect COW_POTION = new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 1);
    
    private final List<LivingEntity> entities = Lists.newArrayList();

    public AnimalCompanion() {
        super("animal_companion", 4000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && condition_spawn(event)) {
            spawn(player);
            COOLDOWN_RESET();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && condition_discard(event)) {
            ((LivingEntity) event.getRightClicked()).setHealth(0);
            event.setCancelled(true);
        }
    }

    private static boolean condition_spawn(EntityDamageByEntityEvent event) {
        return event.getDamageSource().getCausingEntity() instanceof Player && RandomUtil.RANDOM.nextDouble() <= CHANCE;
    }

    private boolean condition_discard(PlayerInteractAtEntityEvent event) {
        if (Tag.ITEMS_PICKAXES.isTagged(event.getPlayer().getEquipment().getItem(event.getHand()).getType())) {
            for (LivingEntity entity : entities) {
                if (entity.getUniqueId().equals(event.getRightClicked().getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void spawn(Player player) {
        switch (RandomUtil.RANDOM.nextInt(6)) {
            case 0 -> // Chicken Jockey
                    EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TEAM_SKELETON, jockey -> {
                        Skeleton skeleton = (Skeleton) jockey;
                        add(skeleton, player);
                        EntityEquipment equipment = skeleton.getEquipment();
                        equipment.setItem(EquipmentSlot.HAND, SKELETON_BOW.build());
                        equipment.setItem(EquipmentSlot.HEAD, ItemBuilder.of(Material.LEATHER_HELMET).setArmorColor(Color.getDye(PLAYER().getTeam().color()).getColor()).build());
                        player.getWorld().spawnEntity(player.getLocation(), EntityType.CHICKEN, CreatureSpawnEvent.SpawnReason.CUSTOM, saddler -> {
                            Chicken chicken = (Chicken) saddler;
                            add(chicken, player);
                            chicken.addPassenger(skeleton);
                        });
                    });
            case 1 -> // Zombie Pigman
                    EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TEAM_ZOMBIFIED_PIGLIN, entity -> {
                        PigZombie pigZombie = (PigZombie) entity;
                        add(pigZombie, player);
                        pigZombie.getEquipment().setItem(EquipmentSlot.HAND, ZOMBIE_PIGMAN_SWORD.build());
                        pigZombie.addPotionEffect(ZOMBIE_PIGMAN_POTION);
                    });
            case 2 -> // Spider
                    EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TEAM_SPIDER, entity -> add(((Spider) entity), player));
            case 3 -> // Exploding Sheep
                    EntityUtil.spawn(player.getLocation(), EntityUtil.Type.EXPLODING_SHEEP, entity -> add(((Sheep) entity), player), player);
            case 4 -> // Cow
                    player.getWorld().spawnEntity(player.getLocation(), EntityType.COW, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                        Cow cow = (Cow) entity;
                        add(cow, player);
                        cow.addPotionEffect(COW_POTION);
                    });
            case 5 -> // Tamed Wolf
                    EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TAMED_WOLF, entity -> {
                        Wolf wolf = (Wolf) entity;
                        add(wolf, player);
                        AttributeInstance maxHealth = wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        maxHealth.setBaseValue(maxHealth.getBaseValue() / 2);
                        wolf.setCollarColor(Color.getDye(PLAYER().getTeam().color()));
                    }, player.getUniqueId());
        }
    }

    private void add(LivingEntity entity, Player player) {
        entities.removeIf(Entity::isDead);
        if (entities.size() >= MAX) {
            entities.removeFirst().setHealth(0);
        }
        entities.add(entity);
        entity.customName(Component.translatable("mw78.entity.tamed", player.name(), entity.name()));
        player.getScoreboard().getPlayerTeam(player).addEntity(entity);
    }

    @Override
    public void unregister() {
        for (LivingEntity entity : entities) {
            entity.setHealth(0);
        }
        entities.clear();
    }
}
