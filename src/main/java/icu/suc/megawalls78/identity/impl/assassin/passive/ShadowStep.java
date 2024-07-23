package icu.suc.megawalls78.identity.impl.assassin.passive;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.impl.assassin.skill.ShadowCloak;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.HealthUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;

public final class ShadowStep extends Passive implements IActionbar {
    private static final long COOLDOWN = 10 * 20;
    boolean inCD = false;

    public ShadowStep() {
        super("shadow_step");
    }

    @EventHandler
    public void onDMGTaken(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if ((event.getDamageSource().getCausingEntity() instanceof Player dmger) && (event.getEntity() instanceof Player player)) {
            if (shouldPassive(player)) {
                if ((!inCD) && !isInShadowCloak(getPlayer())) {

                    if (player.isSneaking() && (player.getLocation().distance(dmger.getLocation()) <= 25)) {
                        event.setCancelled(true);
                        inCD = true;
                        Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> inCD = false, COOLDOWN);
                        ParticleUtil.spawnParticleRandomBody(player, Particle.PORTAL, 8);
                        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.teleport(getBlockBehindPlayer(dmger));
                        ParticleUtil.spawnParticleRandomBody(dmger, Particle.PORTAL, 8);
                        player.getWorld().playSound(dmger.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }


            }
        }
    }

    @EventHandler
    public void onDMGDealt(EntityDamageByEntityEvent event) {
        /*
         * 这一段本来是主skill里面的内容。可是那里面不能写被动
         *
         * */

        if (event.isCancelled()) {
            return;
        }
        if ((event.getDamageSource().getCausingEntity() instanceof Player player) && (event.getEntity() instanceof Player || event.getEntity() instanceof Wither)) {
            if (shouldPassive(player)) {
                if (isInShadowCloak(getPlayer())) {
                    //这是退出shadow cloak的主动取消方式。如果以此方式取消，应该会被返还能量
                    setShadowCloakState(getPlayer(), false);
                    getPlayer().increaseEnergy(Objects.requireNonNull(player.getPotionEffect(PotionEffectType.INVISIBILITY)).getDuration() / 5);
                    // duration/20*4-->duration/5
                    player.removePotionEffect(PotionEffectType.RESISTANCE);
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    HealthUtil.trueDamage((LivingEntity) event.getEntity(), event.getDamage() * 0.1, player);
                }
            }
        }
    }

    private boolean isInShadowCloak(GamePlayer gp) {
        for (Skill s : gp.getIdentity().getSkills().values()) {
            if (s.getClass() == ShadowCloak.class) {
                if (((ShadowCloak) s).inShadowCloak) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setShadowCloakState(GamePlayer gp, boolean state) {
        for (Skill s : gp.getIdentity().getSkills().values()) {
            if (s.getClass() == ShadowCloak.class) {
                ((ShadowCloak) s).inShadowCloak = state;
            }
        }
    }


    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.MODE.accept(Component.text(inCD ? "Not Ready" : "Ready"));
    }

    public Location getBlockBehindPlayer(Player player) {
        Vector inverseDirectionVec = player.getLocation().getDirection().normalize().multiply(-1);
        return player.getLocation().add(inverseDirectionVec);
    }
}
