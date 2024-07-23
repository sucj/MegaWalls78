package icu.suc.megawalls78.identity.impl.cow.passive;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.getIdentity;
import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class RefreshingSip extends Passive implements IActionbar {

    private static final long COOLDOWN = 5000L;
    private static final double RANGE = 7.0D;
    private static final double HEALTH = 4.0D;
    private static final int FOOD = 20;

    private long lastMills;

    public RefreshingSip() {
        super("refreshing_sip");
    }

    @EventHandler
    public void drunkMilk(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (shouldPassive(player) && event.getItem().getType().equals(Material.MILK_BUCKET)) {

            long currentMillis = System.currentTimeMillis();
            if (currentMillis - lastMills >= COOLDOWN) {
                lastMills = currentMillis;

                AtomicInteger count = new AtomicInteger();
                heal(player);
                count.getAndIncrement();

                EntityUtil.getNearbyEntities(player, RANGE).stream()
                        .filter(entity -> entity instanceof Player)
                        .filter(entity -> isValidAllies(player, entity))
                        .filter(entity -> !getIdentity((Player) entity).equals(Identity.COW))
                        .forEach(entity -> {
                            heal((Player) entity);
                            count.getAndIncrement();
                        });

                playSkillEffect(player);

                summaryHeal(player, count.get());
            }
        }
    }

    private void heal(Player player) {
        player.heal(HEALTH);
        player.setFoodLevel(FOOD);
        PlayerUtil.setStarvation(player, FOOD);
        playHealEffect(player);
    }

    private void playHealEffect(Player player) {
        ParticleUtil.spawnParticleOverhead(player, Particle.HEART, 2);
    }

    private void playSkillEffect(Player player) {
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.ENTITY_EFFECT, 64, RANGE, 700L, Color.FUCHSIA);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_COW_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }
}
