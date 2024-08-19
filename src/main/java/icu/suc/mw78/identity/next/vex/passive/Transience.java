package icu.suc.mw78.identity.next.vex.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class Transience extends Passive implements IActionbar {

    private static final double HEALTH = 10.0D;
    private static final double DAMAGE = 1.0D;

    private static final PotionEffect STRENGTH = new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect HASTE = new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 2);

    private static final Effect<Player> EFFECT_PARTICLE = Effect.create(player -> ParticleUtil.spawnParticleRandomBody(player, Particle.DUST, 1, 0, new Particle.DustOptions(Color.RED, 1)));

    private int tick;
    private boolean state;

    public Transience() {
        super("transience");
    }

    @EventHandler
    public void onPLayerTickStart(ServerTickStartEvent event) {
        Player player = PLAYER().getBukkitPlayer();

        double health = player.getHealth();
        state = health < HEALTH;
        if (state) {
            List<PotionEffect> effects = Lists.newArrayList();
            if (!EntityUtil.hasPotionEffect(player, STRENGTH)) {
                player.addPotionEffect(STRENGTH);
                effects.add(STRENGTH);
            }
            if (!EntityUtil.hasPotionEffect(player, SPEED)) {
                player.addPotionEffect(SPEED);
                effects.add(SPEED);
            }
            if (!EntityUtil.hasPotionEffect(player, HASTE)) {
                player.addPotionEffect(HASTE);
                effects.add(HASTE);
            }
            summaryEffectSelf(player, effects);
            if (tick % 40 == 0 && health > DAMAGE) {
                player.damage(DAMAGE, DamageSource.builder(DamageType.STARVE).build());
            }
            EFFECT_PARTICLE.play(player);
            tick++;
        } else {
            if (EntityUtil.hasPotionEffect(player, STRENGTH)) {
                player.removePotionEffect(PotionEffectType.STRENGTH);
            }
            if (EntityUtil.hasPotionEffect(player, SPEED)) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
            if (EntityUtil.hasPotionEffect(player, HASTE)) {
                player.removePotionEffect(PotionEffectType.HASTE);
            }
            tick = 0;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer gamePlayer = event.getPlayer();
        if (PASSIVE(gamePlayer)) {
            if (state) {
                gamePlayer.getBukkitPlayer().setHealth(HEALTH);
            }
        }
    }

    @Override
    public void unregister() {
        tick = 0;
    }

    @Override
    public Component acb() {
        return Type.STATE.accept(state);
    }
}
