package icu.suc.megawalls78.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HealthUtil {
    public static void trueDamage(LivingEntity victim, double amount, Player damager) {
        double health = victim.getHealth();
        if (health >= amount + 0.01) {
            victim.damage(0.01, damager);
            victim.setHealth(health - amount);
        } else {
            victim.damage(250, damager);
        }
    }
}
