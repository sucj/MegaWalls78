package icu.suc.megawalls78.identity.impl.zombie.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Toughness extends Passive implements IActionbar {

    private static final int MAX = 3;
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 20, 0);

    private int hit = MAX;

    public Toughness() {
        super("toughness");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && event.getDamageSource().getCausingEntity() != null) {
            if (shouldPassive(player)) {
                if (hit++ >= MAX) {
                    player.addPotionEffect(RESISTANCE);
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
