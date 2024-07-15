package icu.suc.megawalls78.identity.impl.herebrine.skill;

import icu.suc.megawalls78.entity.HerobrineLightning;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class Wrath extends Skill {

    private static final double RANGE = 5.0D;

    public Wrath() {
        super("wrath", 100, 1000L);
    }

    @Override
    protected void use0(Player player) {
        AtomicInteger count = new AtomicInteger();
        player.getNearbyEntities(RANGE, RANGE, RANGE).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    EntityUtil.spawn(entity.getLocation(), EntityUtil.Type.HEROBRINE_LIGHTNING, lightning -> ((HerobrineLightning) lightning.getHandle()).setTarget(entity));
                    count.getAndIncrement();
                });
        int i = count.get();
        if (i == 0) {
            EntityUtil.spawn(player.getLocation(), EntityUtil.Type.HEROBRINE_LIGHTNING);
        }
        summaryHit(player, i);
    }
}
