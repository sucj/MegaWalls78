package icu.suc.megawalls78.identity.impl.spider.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class VenomStrike extends Passive implements IActionbar {

    private static final int MAX = 4;
    private static final long COOLDOWN = 7000L;
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 125, 0);

    private int hit = MAX;
    private long lastMills;

    public VenomStrike() {
        super("venom_strike");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && event.getDamageSource().getCausingEntity() instanceof Player damager) {
            if (shouldPassive(damager) && !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                long currentMillis = System.currentTimeMillis();
                if (currentMillis - lastMills >= COOLDOWN && hit++ >= MAX) {
                    player.addPotionEffect(POISON);
                    lastMills = currentMillis;
                    hit = 1;
                }
            }
        }
    }

    @Override
    public void unregister() {
        hit = MAX;
    }

    @Override
    public Component acbValue() {
        return Type.COMBO_COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN, hit, MAX);
    }
}
