package icu.suc.mw78.identity.regular.shaman.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Lists;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargeCooldownPassive;
import icu.suc.megawalls78.util.Color;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

@Trait(value = "wolf_pack", cooldown = 4000L, charge = 6)
public final class WolfPack extends ChargeCooldownPassive {

    private static final int MAX = 3;
    private static final double HEALTH = 14;
    private static final double SCALE = 0.65;
    private static final int LIVE = 240;

    private final List<LivingEntity> entities = Lists.newArrayList();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && CHARGE()) {
            if (entities.size() >= MAX) {
                entities.removeFirst().setHealth(0);
            }
            EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TAMED_WOLF, entity -> {
                Wolf wolf = (Wolf) entity;
                EntityUtil.setTamed(player, wolf);
                EntityUtil.setAttributeValue(wolf, Attribute.MAX_HEALTH, HEALTH);
                EntityUtil.scaleAttributeBaseValue(wolf, Attribute.ATTACK_DAMAGE, SCALE);
                wolf.setCollarColor(Color.getDye(PLAYER().getTeam().color()));
                entities.add(wolf);
            }, player.getUniqueId());
            CHARGE_RESET();
            COOLDOWN_RESET();
        }
    }

    @EventHandler
    public void onServerTick(ServerTickStartEvent event) {
        entities.removeIf(entity -> {
            boolean flag = entity.getTicksLived() > LIVE;
            if (flag) {
                entity.setHealth(0);
            }
            return flag;
        });
    }

    @Override
    public void unregister() {
        for (LivingEntity entity : entities) {
            entity.setHealth(0);
        }
        entities.clear();
    }
}
