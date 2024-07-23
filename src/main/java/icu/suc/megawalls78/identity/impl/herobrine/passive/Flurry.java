package icu.suc.megawalls78.identity.impl.herobrine.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Flurry extends Passive implements IActionbar {

    private static final int MAX = 3;
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 60, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0);

    private int hit = MAX;

    public Flurry() {
        super("flurry");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamageSource().getCausingEntity() instanceof Player player) {
            if (shouldPassive(player) && !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                if (hit++ >= MAX) {
                    player.addPotionEffect(SPEED);
                    player.addPotionEffect(REGENERATION);
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
    public Component acb() {
        return Type.COMBO.accept(hit, MAX);
    }
}
