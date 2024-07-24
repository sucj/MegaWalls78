package icu.suc.megawalls78.identity.impl.assassin.passive;

import icu.suc.megawalls78.identity.impl.assassin.skill.ShadowCloak;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ParticleUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public final class ShadowStep extends Passive implements IActionbar {

    private static final long COOLDOWN = 200L;
    private static final double RANGE = 25.0D; // 25格内的远程伤害

    private long lastMills;

    public ShadowStep() {
        super("shadow_step");
    }

    @EventHandler
    public void damaged(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player player && shouldPassive(player) // 无论何时都应该改先判断shouldPassive
                && event.getDamageSource().getCausingEntity() instanceof Player damager) {
            // 这三句是当前判断是否cd的通用代码
            long currentMillis = System.currentTimeMillis();
            if (currentMillis - lastMills >= COOLDOWN) {
                lastMills = currentMillis;

                // 一般情况下，计算cd后再进入技能触发条件判断
                if (player.isSneaking() && player.getLocation().distance(damager.getLocation()) <= RANGE && !ShadowCloak.getState(player.getUniqueId())) {
                    player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    player.teleport(getBlockBehindPlayer(damager));
                    ParticleUtil.spawnParticleRandomBody(damager, Particle.PORTAL, 8);
                    player.getWorld().playSound(damager.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
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

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }

    private Location getBlockBehindPlayer(Player player) {
        Vector inverseDirectionVec = player.getLocation().getDirection().normalize().multiply(-1);
        return player.getLocation().add(inverseDirectionVec);
    }
}
