package icu.suc.mw78.identity.regular.squid.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait("squid_splash")
public class SquidSplash extends Skill {

    private static final double RADIUS = 5.25D;
    private static final double DAMAGE = 3.5D;
    private static final double SCALE = 0.7D;
    private static final double HEALTH = DAMAGE * SCALE;
    private static final double MAX = 7.0D;

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.playContractingCircleParticle(player.getLocation(), Particle.SPLASH, 64, RADIUS, 525L);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });

    public SquidSplash() {
        super(100, 2000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !(entity instanceof Wither))
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    pull((LivingEntity) entity, player);
                    count.getAndIncrement();
                });

        int i = count.get();
        if (i == 0) {
            return noTarget(player);
        }

        heal(player, i);
        EFFECT_SKILL.play(player);

        return summaryHit(player, i);
    }

    private static void pull(LivingEntity entity, Player player) {
        entity.damage(DAMAGE, DamageSource.of(DamageType.DROWN, player));
        Vector vector = EntityUtil.getPullVector(player, entity, true);
        double y = vector.getY();
        if (y > 0) {
            vector.setY(Math.min(y, 0.2D));
        } else {
            vector.setY(Math.max(y, -0.2D));
        }
        entity.setVelocity(vector);
    }

    private static void heal(Player player, int count) {
        player.heal(Math.min(count * HEALTH, MAX));
    }
}
