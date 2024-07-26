package icu.suc.megawalls78.identity.impl.herobrine.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Wrath extends Skill {

    private static final double RADIUS = 5.0D;

    public Wrath() {
        super("wrath", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    thunder(entity);
                    count.getAndIncrement();
                });

        int i = count.get();
        if (i == 0) {
            return noTarget(player);
        }

        return summaryHit(player, i);
    }

    private static void thunder(Entity entity) {
        EntityUtil.spawn(entity.getLocation(), EntityUtil.Type.HEROBRINE_LIGHTNING, null, entity);
    }
}
