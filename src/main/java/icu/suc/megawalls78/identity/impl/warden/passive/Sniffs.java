package icu.suc.megawalls78.identity.impl.warden.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Sniffs extends Passive implements IActionbar {

    private static final long COOLDOWN = 10000L;
    private static final double RANGE = 20.0D;
    private static final int MAX = 10;

    private long lastMills;

    public Sniffs() {
        super("sniffs");
    }

    @EventHandler
    public void tickStart(ServerTickStartEvent event) {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - lastMills >= COOLDOWN) {
            lastMills = currentMillis;
            sniff();
        }
    }

    private void sniff() {
        GamePlayer gamePlayer = getPlayer();
        Player player = gamePlayer.getBukkitPlayer();
        AtomicInteger energy = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RANGE).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    energy.getAndIncrement();
                    playSniffedEffect(entity, player);
                });
        gamePlayer.increaseEnergy(Math.min(energy.get(), MAX));
        playSoundEffect(player);
    }

    private void playSniffedEffect(Entity entity, Player player) {
        ParticleUtil.spawnParticle(entity.getWorld(), Particle.VIBRATION, entity.getLocation(), 1, new Vibration(new Vibration.Destination.EntityDestination(player), 20));
    }

    private void playSoundEffect(Player player) {
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WARDEN_LISTENING, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }
}
