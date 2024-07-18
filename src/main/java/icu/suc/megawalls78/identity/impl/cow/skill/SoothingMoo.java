package icu.suc.megawalls78.identity.impl.cow.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class SoothingMoo extends Skill {

    private static final double RANGE = 7.0D;

    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 50, 0);
    private static final PotionEffect REGENERATION_2 = new PotionEffect(PotionEffectType.REGENERATION, 50, 1);
    private static final PotionEffect REGENERATION_3 = new PotionEffect(PotionEffectType.REGENERATION, 50, 2);

    public SoothingMoo() {
        super("soothing_moo", 100, 1000L);
    }

    @Override
    protected void use0(Player player) {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RANGE, 500L, Color.FUCHSIA);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F, 0);

        AtomicInteger count = new AtomicInteger();
        player.addPotionEffect(RESISTANCE);
        player.addPotionEffect(REGENERATION_2);
        ParticleUtil.spawnParticleOverhead(player, Particle.HEART, 2);
        count.incrementAndGet();

        player.getNearbyEntities(RANGE, RANGE, RANGE).stream()
                .filter(entity -> isValidAllies(player, entity))
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> {
                    ((Player) entity).addPotionEffect(REGENERATION_3);
                    ParticleUtil.spawnParticleOverhead(((Player) entity), Particle.HEART, 3);
                    count.getAndIncrement();
                });
        summaryHeal(player, count.get());
    }
}
