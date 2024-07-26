package icu.suc.megawalls78.identity.impl.cow.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class SoothingMoo extends Skill {

    private static final double RADIUS = 7.0D;

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 50, 0);
    private static final PotionEffect REGENERATION_2 = new PotionEffect(PotionEffectType.REGENERATION, 50, 1);
    private static final PotionEffect REGENERATION_3 = new PotionEffect(PotionEffectType.REGENERATION, 50, 2);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RADIUS, 700L, Color.FUCHSIA);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_COW_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F, 0);
    });
    private static final Effect<Pair<Player, Integer>> EFFECT_HEAL = Effect.create(pair -> {
        Player player = pair.getLeft();
        Integer count = pair.getRight();
        ParticleUtil.spawnParticleOverhead(player, Particle.HEART, count);
    });

    public SoothingMoo() {
        super("soothing_moo", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        healSelf(player);
        count.incrementAndGet();

        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> isValidAllies(player, entity))
                .forEach(entity -> {
                    healOther((Player) entity);
                    count.getAndIncrement();
                });

        EFFECT_SKILL.play(player);

        return summaryHeal(player, count.get());
    }

    private static void healSelf(Player player) {
        player.addPotionEffect(RESISTANCE);
        player.addPotionEffect(REGENERATION_2);
        EFFECT_HEAL.play(Pair.of(player, 2));
    }

    private static void healOther(Player player) {
        player.addPotionEffect(REGENERATION_3);
        EFFECT_HEAL.play(Pair.of(player, 3));
    }
}
