package icu.suc.mw78.identity.regular.hunter.passive;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Color;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

@Trait(value = "animal_companion", cooldown = 4000L)
public final class AnimalCompanion extends CooldownPassive {

    private static final double CHANCE = 0.2D;

    private static final ItemBuilder ZOMBIE_PIGMAN_SWORD = ItemBuilder.of(Material.GOLDEN_SWORD);
    private static final ItemBuilder SKELETON_BOW = ItemBuilder.of(Material.BOW);

    private static final PotionEffect ZOMBIE_PIGMAN_POTION = new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect COW_POTION = new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 1);
    
    private final List<LivingEntity> entities = Lists.newArrayList();

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
        Location location = player.getLocation();
        UUID uuid = player.getUniqueId();
        switch (RandomUtil.RANDOM.nextInt(6)) {
            case 0 -> // Chicken Jockey
                    EntityUtil.spawn(location, EntityUtil.Type.TAMED_SKELETON, jockey -> {
                        Skeleton skeleton = (Skeleton) jockey;
                        EntityEquipment equipment = skeleton.getEquipment();
                        equipment.setItem(EquipmentSlot.HAND, SKELETON_BOW.build());
                        equipment.setItem(EquipmentSlot.HEAD, ItemBuilder.of(Material.LEATHER_HELMET).setArmorColor(Color.getDye(PLAYER().getTeam().color()).getColor()).build());
                        EntityUtil.spawn(location, EntityUtil.Type.TAMED_CHICKEN, saddler -> {
                            Chicken chicken = (Chicken) saddler;
                            add(player, chicken, skeleton);
                            chicken.addPassenger(skeleton);
                        }, uuid);
                    }, uuid);
            case 1 -> // Zombie Pigman
                    EntityUtil.spawn(location, EntityUtil.Type.TAMED_ZOMBIFIED_PIGLIN, entity -> {
                        PigZombie pigZombie = (PigZombie) entity;
                        add(player, pigZombie);
                        pigZombie.getEquipment().setItem(EquipmentSlot.HAND, ZOMBIE_PIGMAN_SWORD.build());
                        pigZombie.addPotionEffect(ZOMBIE_PIGMAN_POTION);
                    }, uuid);
            case 2 -> // Spider
                    EntityUtil.spawn(location, EntityUtil.Type.TAMED_SPIDER, entity -> add(player, ((Spider) entity)), uuid);
            case 3 -> // Exploding Sheep
                    EntityUtil.spawn(location, EntityUtil.Type.EXPLODING_SHEEP, entity -> add(player, (Sheep) entity), player);
            case 4 -> // Cow
                    EntityUtil.spawn(location, EntityUtil.Type.TAMED_COW, entity -> {
                        Cow cow = (Cow) entity;
                        add(player, cow);
                        cow.addPotionEffect(COW_POTION);
                    }, uuid);
            case 5 -> // Tamed Wolf
                    EntityUtil.spawn(location, EntityUtil.Type.TAMED_WOLF, entity -> {
                        Wolf wolf = (Wolf) entity;
                        add(player, wolf);
                        EntityUtil.scaleAttributeBaseValue(wolf, Attribute.MAX_HEALTH, 0.5D);
                        wolf.setCollarColor(Color.getDye(PLAYER().getTeam().color()));
                    }, uuid);
        }
    }

    private void add(Player player, LivingEntity... livings) {
        for (LivingEntity living : entities) {
            if (living.isDead()) {
                continue;
            }
            living.setHealth(0);
        }
        entities.clear();
        for (LivingEntity living : livings) {
            entities.add(living);
            EntityUtil.setTamed(player, living);
        }
    }

    @Override
    public void unregister() {
        for (LivingEntity entity : entities) {
            entity.setHealth(0);
        }
        entities.clear();
    }
}
