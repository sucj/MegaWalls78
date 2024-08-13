package icu.suc.megawalls78.identity.impl.cow.passive;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.getIdentity;
import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class RefreshingSip extends CooldownPassive {

    private static final double RADIUS = 7.0D;
    private static final double HEALTH = 4.0D;
    private static final int FOOD = 20;

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RADIUS, 700L, Color.FUCHSIA);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_COW_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });
    private static final Effect<Player> EFFECT_HEAL = Effect.create(player -> {
        ParticleUtil.spawnParticleOverhead(player, Particle.HEART, 2);
    });

    public RefreshingSip() {
        super("refreshing_sip", 5000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsumeMilk(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && COOLDOWN() && condition(event)) {
            AtomicInteger count = new AtomicInteger();
            heal(player);
            count.getAndIncrement();

            EntityUtil.getNearbyEntities(player, RADIUS).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> isValidAllies(player, entity))
                    .filter(entity -> !getIdentity((Player) entity).equals(Identity.COW))
                    .forEach(entity -> {
                        heal((Player) entity);
                        summaryHealBy(player, (Player) entity);
                        count.getAndIncrement();
                    });

            EFFECT_SKILL.play(player);

            summaryHeal(player, count.get());

            COOLDOWN_RESET();
        }
    }

    private static boolean condition(PlayerItemConsumeEvent event) {
        return event.getItem().getType().equals(Material.MILK_BUCKET);
    }

    private static void heal(Player player) {
        player.heal(HEALTH);
        player.setFoodLevel(FOOD);
        PlayerUtil.setStarvation(player, FOOD);
        EFFECT_HEAL.play(player);
    }
}
