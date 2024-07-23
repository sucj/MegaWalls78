package icu.suc.megawalls78.identity.impl.squid.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rejuvenate extends Passive implements IActionbar {

    private static final long COOLDOWN = 40000L;
    private static final double HEALTH = 21.0D;
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 30, 4);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 30, 0);

    private long lastMills;

    public Rejuvenate() {
        super("rejuvenate");
    }

    @EventHandler
    public void onPlayerTickStart(ServerTickStartEvent event) {
        long currentMillis = System.currentTimeMillis();
        Player player = getPlayer().getBukkitPlayer();
        if (currentMillis - lastMills >= COOLDOWN && player.getHealth() < HEALTH) {
            lastMills = currentMillis;
            player.addPotionEffect(REGENERATION);
            player.addPotionEffect(RESISTANCE);
        }
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }
}
