package icu.suc.mw78.identity.regular.cow.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait(value = "soothing_moo", cost = 100F, cooldown = 1000L)
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

    @Override
    protected boolean use0(Player player) {
        healSelf(player);

        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> isValidAllies(player, entity))
                .forEach(entity -> healOther(player, (Player) entity));

        EFFECT_SKILL.play(player);

        return summaryEffectSelf(player, RESISTANCE, REGENERATION_2);
    }

    private void healSelf(Player player) {
        player.addPotionEffect(RESISTANCE);
        player.addPotionEffect(REGENERATION_2);
        EFFECT_HEAL.play(Pair.of(player, 2));
    }

    private void healOther(Player player, Player target) {
        target.addPotionEffect(REGENERATION_3);
        EFFECT_HEAL.play(Pair.of(target, 3));
        summaryEffectOther(player, target, REGENERATION_3);
    }
}
