package icu.suc.megawalls78.identity.impl.mythic.werewolf.skill;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Lycanthropy extends DurationSkill {

    private static final long DURATION = 6000L;
    private static final int TICK = (int) (DURATION / 50);
    private static final double RADIUS = 5.0D;
    private static final double MIN_DAMAGE = 1.0D;
    private static final double MAX_DAMAGE = 5.0D;
    private static final int HUNGER = 1;
    private static final double SCALE = 0.25D;
    private static final double MAX_HEAL = 10.0D;
    private static final double DAMAGE = 1.67D;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, TICK, 1);

    private static final Effect<Player> EFFECT_START = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WOLF_HOWL, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Player> EFFECT_END = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WOLF_WHINE, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> ParticleUtil.spawnParticleRandomBody(player, Particle.SMOKE, 1, 0));
    private static final Effect<Entity> EFFECT_HIT = Effect.create(entity -> ParticleUtil.spawnParticleRandomBody(entity, Particle.BLOCK, 4, 1, Material.REDSTONE_BLOCK.createBlockData()));

    private Task task;

    public Lycanthropy() {
        super("lycanthropy", 100, 6000L, 6000L, Internal.class);
    }

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_START.play(player);

        potion(player);
        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }
        return true;
    }

    private void potion(Player player) {
        player.addPotionEffect(SPEED);
        summaryEffectSelf(player, SPEED);
    }

    private final class Task extends DurationTask {

        private Task(Player player) {
            super(player, TICK);

            EntityUtil.setMetadata(player, getId(), true);
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            if (tick % 2 == 0) {
                EFFECT_SKILL.play(player);
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            Internal passive = (Internal) Lycanthropy.this.getPassive();
            Set<UUID> unique = passive.getUnique();
            double damage = Math.min(Math.max(unique.size() * DAMAGE, MIN_DAMAGE), MAX_DAMAGE);
            AtomicInteger count = new AtomicInteger();
            AtomicDouble heal = new AtomicDouble();
            EntityUtil.getNearbyEntities(player, RADIUS).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        Player victim = (Player) entity;
                        victim.damage(damage, DamageSource.of(DamageType.MOB_ATTACK, player));
                        PlayerUtil.decreaseFoodLevel(victim, HUNGER);
                        count.getAndIncrement();
                        heal.addAndGet(damage * SCALE);
                    });
            double remain = MAX_HEAL - passive.getHeal();
            double v = heal.get();
            player.heal(Math.min(remain, v));
            unique.clear();
            passive.setHeal(0);
            EFFECT_END.play(player);
            EntityUtil.removeMetadata(player, getId());
            summaryHit(player, count.get());
            super.cancel();
            stop();
        }
    }

    public static final class Internal extends Passive {

        private final Set<UUID> unique = Sets.newHashSet();
        private double heal;

        public Internal() {
            super("lycanthropy");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerAttack(EntityDamageByEntityEvent event) {
            if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && EntityUtil.getMetadata(player, getId()) && condition(event)) {
                double heal = event.getFinalDamage() * SCALE;
                double remain = MAX_HEAL - this.heal;
                if (remain > heal) {
                    player.heal(heal);
                    this.heal += heal;
                } else {
                    player.heal(remain);
                    this.heal += remain;
                }
                Entity entity = event.getEntity();
                UUID uuid = entity.getUniqueId();
                if (!unique.contains(uuid)) {
                    unique.add(uuid);
                    EFFECT_HIT.play(entity);
                    summaryUnique(player, unique.size());
                }
            }
        }

        private static boolean condition(EntityDamageByEntityEvent event) {
            return event.getEntity() instanceof Player && (EntityUtil.isMeleeAttack(event) || EntityUtil.isArrowAttack(event) || EntityUtil.isSweepAttack(event));
        }

        public Set<UUID> getUnique() {
            return unique;
        }

        public double getHeal() {
            return heal;
        }

        public void setHeal(double heal) {
            this.heal = heal;
        }
    }
}
