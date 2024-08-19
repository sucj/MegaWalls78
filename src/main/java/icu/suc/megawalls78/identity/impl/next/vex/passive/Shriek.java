package icu.suc.megawalls78.identity.impl.next.vex.passive;

import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class Shriek extends CooldownPassive {

    private static final double SCALE = 1.5D;

    private static final Effect<Player> EFFECT_SOUND = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VEX_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public Shriek() {
        super("shriek", 3000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getDirectEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() && !EntityUtil.isEntityInFront(event.getEntity(), player)) {
            event.setDamage(event.getDamage() * SCALE);
            EFFECT_SOUND.play(player);
            COOLDOWN_RESET();
        }
    }
}
