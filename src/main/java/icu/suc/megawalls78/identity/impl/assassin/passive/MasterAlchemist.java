package icu.suc.megawalls78.identity.impl.assassin.passive;

import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MasterAlchemist extends CooldownPassive {

    private static final long TIME = 1000L;
    private static final double DAMAGE = 10.0D;
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 2);

    private final MutablePair<Long, Double> lastDamage = MutablePair.of(0L, 0.0D);

    public MasterAlchemist() {
        super("master_alchemist", 12000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) { // 实际上这个技能不需要在意是谁造成了伤害
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN()) {
            double damage = event.getFinalDamage();
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastDamage.getLeft() <= TIME) {
                lastDamage.setRight(lastDamage.getRight() + damage);
            } else {
                lastDamage.setLeft(currentTimeMillis);
                lastDamage.setRight(damage);
            }

            if (lastDamage.getRight() > DAMAGE) {
                COOLDOWN_RESET();
                player.addPotionEffect(REGENERATION);
                summaryEffectSelf(player, REGENERATION);
                lastDamage.setLeft(0L);
            }
        }
    }
}
