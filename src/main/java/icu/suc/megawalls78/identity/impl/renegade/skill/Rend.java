package icu.suc.megawalls78.identity.impl.renegade.skill;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Rend extends Skill {

    private static final double RADIUS = 8.0D;
    private static final int PIN_MIN = 60000;
    private static final int PIN_MAX = 180000;
    private static final double DAMAGE_PER = 2.0D;
    private static final double DAMAGE_MAX = 18.0D;
    private static final int RETRIEVE = 6;
    private static final int ENERGY = 100;

    public Rend() {
        super("rend", 100, 1000L, Internal.class);
    }

    @Override
    protected boolean use0(Player player) {

        List<Map.Entry<Player, Integer>> list = Lists.newArrayList();
        Map<UUID, List<Long>> arrows = ((Internal) getPassive()).getArrows();
        long current = System.currentTimeMillis();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    Player victim = (Player) entity;
                    List<Long> timestamps = arrows.get(victim.getUniqueId());
                    if (timestamps != null && !timestamps.isEmpty() && current <= timestamps.getLast() + PIN_MIN) {
                        int retrieve = Math.min(timestamps.size(), RETRIEVE);
                        player.getInventory().addItem(ItemStack.of(Material.ARROW, retrieve));
                        timestamps.subList(0, retrieve).clear();
                        list.add(new AbstractMap.SimpleEntry<>(victim, retrieve));
                    }
                });
        if (list.isEmpty()) {
            return noTarget(player);
        }
        list.sort((o1, o2) -> {
            int compare = Integer.compare(o2.getValue(), o1.getValue());
            if (compare != 0) {
                return compare;
            }
            return Double.compare(o1.getKey().getHealth(), o2.getKey().getHealth());
        });
        Map<Player, Double> map = Maps.newHashMap();
        double total = 0;
        for (Map.Entry<Player, Integer> entry : list) {
            double damage = entry.getValue() * DAMAGE_PER;
            double remain = DAMAGE_MAX - total;
            if (damage > remain) {
                damage = remain;
            }
            if (damage == 0) {
                continue;
            }
            total += damage;
            map.put(entry.getKey(), damage);
        }
        boolean energy = false;
        int count = 0;
        for (Player victim : map.keySet()) {
            victim.damage(map.get(victim), DamageSource.of(DamageType.GENERIC_KILL, player));
            if (victim.getHealth() <= 0) {
                energy = true;
            }
            count++;
        }
        if (energy) {
            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> refund(player, ENERGY));
        }
        return summaryHit(player, count);
    }

    public static final class Internal extends Passive {

        private final Map<UUID, List<Long>> arrows = Maps.newHashMap();

        public Internal() {
            super("rend");
        }

        @EventHandler
        public void onPlayerAttack(EntityDamageByEntityEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (event.getDamageSource().getCausingEntity() instanceof Player player && PASSIVE(player) && condition(event)) {
                Player victim = (Player) event.getEntity();
                List<Long> timestamps = arrows.computeIfAbsent(victim.getUniqueId(), uuid -> Lists.newArrayList());
                long current = System.currentTimeMillis();
                timestamps.removeIf(timestamp -> current > timestamp + PIN_MAX);
                timestamps.add(current);
                summaryArrows(player, victim, timestamps.size());
            }
        }

        public Map<UUID, List<Long>> getArrows() {
            return arrows;
        }

        private static boolean condition(EntityDamageByEntityEvent event) {
            return event.getEntity() instanceof Player && event.getDamager() instanceof AbstractArrow;
        }

        @Override
        public void unregister() {
            arrows.clear();
        }
    }
}
