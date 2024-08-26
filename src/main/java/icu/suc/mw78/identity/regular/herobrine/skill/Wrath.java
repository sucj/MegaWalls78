package icu.suc.mw78.identity.regular.herobrine.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

@Trait(value = "wrath", cost = 100F, cooldown = 1000L)
public final class Wrath extends Skill {

    private static final double RADIUS = 5.0D;
    private static final double DAMAGE = 5.0D;

    @Override
    protected boolean use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    thunder(player, (Player) entity);
                    count.getAndIncrement();
                });

        int i = count.get();
        if (i == 0) {
            return noTarget(player);
        }

        return summaryHit(player, i);
    }

    private static void thunder(Player player, Player target) {
        EntityUtil.spawn(target.getLocation(), EntityUtil.Type.FAKE_LIGHTNING);
        target.damage(DAMAGE, DamageSource.of(DamageType.LIGHTNING_BOLT, player));
    }
}
