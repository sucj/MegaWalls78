package icu.suc.megawalls78.identity.impl.squid.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rejuvenate extends CooldownPassive {

    private static final double HEALTH = 21.0D;

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 30, 4);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 30, 0);

    public Rejuvenate() {
        super("rejuvenate", 40000L);
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        if (COOLDOWN()) {
            Player player = PLAYER().getBukkitPlayer();
            if (condition(player)) {
                potion(player);
                COOLDOWN_RESET();
            }
        }
    }

    private static boolean condition(Player player) {
        return player.getHealth() < HEALTH;
    }

    private static void potion(Player player) {
        player.addPotionEffect(REGENERATION);
        player.addPotionEffect(RESISTANCE);
    }
}
