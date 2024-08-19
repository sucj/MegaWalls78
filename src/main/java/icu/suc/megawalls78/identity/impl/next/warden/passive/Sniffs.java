package icu.suc.megawalls78.identity.impl.next.warden.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Vibration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public final class Sniffs extends CooldownPassive {

    private static final double RADIUS = 20.0D;
    private static final int PER = 2;
    private static final int MAX = 20;

    private static final Effect<Player> EFFECT_SOUND = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WARDEN_LISTENING, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Pair<Entity, Player>> EFFECT_SNIFFED = Effect.create(pair -> {
        Entity entity = pair.getLeft();
        Player player = pair.getRight();
        ParticleUtil.spawnParticle(entity.getWorld(), Particle.VIBRATION, entity.getLocation(), 1, new Vibration(new Vibration.Destination.EntityDestination(player), 20));
    });

    public Sniffs() {
        super("sniffs", 10000L);
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        if (COOLDOWN()) {
            sniff();
            COOLDOWN_RESET();
        }
    }

    private void sniff() {
        GamePlayer gamePlayer = PLAYER();
        Player player = gamePlayer.getBukkitPlayer();
        AtomicInteger energy = new AtomicInteger();
        EntityUtil.getNearbyEntities(player, RADIUS).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> !isValidAllies(player, entity))
                .forEach(entity -> {
                    energy.getAndAdd(PER);
                    EFFECT_SNIFFED.play(Pair.of(entity, player));
                });

        int i = energy.get();
        if (i == 0) {
            return;
        }

        gamePlayer.increaseEnergy(Math.min(i, MAX));

        EFFECT_SOUND.play(player);
    }
}
