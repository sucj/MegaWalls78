package icu.suc.megawalls78.identity.impl.assassin.passive;

import icu.suc.megawalls78.identity.impl.assassin.skill.ShadowCloak;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public final class ShadowStep extends CooldownPassive {

    private static final double RADIUS = 25.0D; // 25格内的远程伤害

    public ShadowStep() {
        super("shadow_step", 10000L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && PASSIVE(player) && COOLDOWN() &&
                event.getDamageSource().getCausingEntity() instanceof Player damager &&
                player.isSneaking() &&
                player.getLocation().distance(damager.getLocation()) <= RADIUS &&
                !ShadowCloak.getState(player.getUniqueId())) {

            player.setFallDistance(0);
            player.teleport(getBlockBehindPlayer(damager));

            event.setCancelled(true);

            COOLDOWN_RESET();
        }
    }

//    @EventHandler
//    public void onDMGDealt(EntityDamageByEntityEvent event) {
//        /*
//         * 这一段本来是主skill里面的内容。可是那里面不能写被动
//         *
//         * */
//
//        if (event.isCancelled()) {
//            return;
//        }
//        if ((event.getDamageSource().getCausingEntity() instanceof Player player) && (event.getEntity() instanceof Player || event.getEntity() instanceof Wither)) {
//            if (shouldPassive(player)) {
//                if (isInShadowCloak(getPlayer())) {
//                    //这是退出shadow cloak的主动取消方式。如果以此方式取消，应该会被返还能量
//                    setShadowCloakState(getPlayer(), false);
//                    getPlayer().increaseEnergy(Objects.requireNonNull(player.getPotionEffect(PotionEffectType.INVISIBILITY)).getDuration() / 5);
//                    // duration/20*4-->duration/5
//                    player.removePotionEffect(PotionEffectType.RESISTANCE);
//                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
//                    HealthUtil.trueDamage((LivingEntity) event.getEntity(), event.getDamage() * 0.1, player);
//                }
//            }
//        }
//    }

    private Location getBlockBehindPlayer(Player player) { // TODO check to is safe
        Vector inverseDirectionVec = player.getLocation().getDirection().normalize().multiply(-1);
        return player.getLocation().add(inverseDirectionVec);
    }
}
