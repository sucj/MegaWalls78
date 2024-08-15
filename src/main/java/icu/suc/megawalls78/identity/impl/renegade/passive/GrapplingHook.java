package icu.suc.megawalls78.identity.impl.renegade.passive;

import icu.suc.megawalls78.event.GrapplingHookEvent;
import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.DurationCooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class GrapplingHook extends DurationCooldownPassive {

    private static final int ENERGY = 60;
    private static final int REFUND = 57;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 0);

//    private static final Effect<Player> EFFECT_BREAK = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 0.4F, 1.0F));
//    private static final Effect<Player> EFFECT_REPAIR = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 0.4F, 1.0F));

    private boolean noFall;

    public GrapplingHook() {
        super("grappling_hook", 15000L, 4000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCast(GrapplingHookEvent event) {
        if (PASSIVE(event.getPlayer()) && event.getState().equals(GrapplingHookEvent.State.CAST)) {
            if (COOLDOWN()) {
                event.setCancelled(PLAYER().getEnergy() < ENERGY);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPull(GrapplingHookEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && event.getState().equals(GrapplingHookEvent.State.PULL)) {

            GamePlayer gamePlayer = PLAYER();
            if (gamePlayer.getEnergy() < ENERGY) {
                event.setCancelled(true);
                return;
            }

            noFall = true;
            gamePlayer.decreaseEnergy(ENERGY);

//            EFFECT_BREAK.play(player);

            DURATION_RESET_ONLY();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && condition_attack(event)) {
            if (DURATION()) {
                refund(player, REFUND);
                player.addPotionEffect(SPEED);
                DURATION_END();
            }
            if (repair(player)) {
//                EFFECT_REPAIR.play(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        if (PASSIVE(event.getPlayer())) {
            COOLDOWN_END();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAssist(IncreaseStatsEvent.Assist event) {
        if (PASSIVE(event.getPlayer())) {
            COOLDOWN_END();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && noFall && condition_fall(event)) {
            noFall = false;
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (PASSIVE(event.getPlayer()) && noFall && condition_fall(event)) {
            noFall = false;
        }
    }

    private static boolean condition_attack(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && EntityUtil.isMeleeAttack(event);
    }

    private static boolean condition_fall(EntityDamageEvent event) {
        return event.getCause().equals(EntityDamageEvent.DamageCause.FALL);
    }

    private static boolean condition_fall(PlayerMoveEvent event) {
        return EntityUtil.isOnGround(event.getPlayer());
    }

    private static boolean repair(Player player) {
        boolean repaired = false;
        for (ItemStack itemStack : player.getInventory()) {
            if (ItemUtil.isMW78Item(itemStack, ItemUtil.GRAPPLING_HOOK)) {
                if (itemStack.getItemMeta() instanceof Damageable damageable) {
                    if (damageable.getDamage() == 0) {
                        continue;
                    }
                    damageable.setDamage(0);
                    itemStack.setItemMeta(damageable);
                    repaired = true;
                }
            }
        }
        return repaired;
    }

    @Override
    public void unregister() {
        noFall = false;
        DURATION_END();
    }
}
