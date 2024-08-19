package icu.suc.megawalls78.identity.impl.hunter.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.trait.passive.DurationCooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class ForceOfNature extends DurationCooldownPassive {

    private static final double SCALE = 2.0D;
    private static final long PER = 1000L;

    private static final Set<PotionEffect[]> PREPARE = Set.of(
            new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 220, 0), new PotionEffect(PotionEffectType.REGENERATION, 220, 0)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.RESISTANCE, 140, 0)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.HASTE, 300, 2)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION, 100, 2)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 160, 1)});
    private static final Set<PotionEffect[]> WALLS_FALL = Set.of(
            new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 220, 0), new PotionEffect(PotionEffectType.REGENERATION, 220, 0)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.RESISTANCE, 140, 0)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION, 100, 2)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.ABSORPTION, 160, 1)},
            new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, 160, 1)});

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public ForceOfNature() {
        super("force_of_nature", 20000L, 5000L);
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        if (COOLDOWN()) {
            potion(PLAYER().getBukkitPlayer());
            DURATION_RESET_ONLY();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && PASSIVE(player) && DURATION() && condition(event)) {
            scale(event);
            DURATION(PER);
        }
    }

    private static boolean condition(EntityDamageByEntityEvent event) {
        return EntityUtil.isMeleeAttack(event) || EntityUtil.isSweepAttack(event);
    }

    private static void scale(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * SCALE);
    }

    private static void potion(Player player) {
        PotionEffect[] effects;
        if (MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING)) {
            effects = RandomUtil.getRandomEntry(PREPARE);
        } else {
            effects = RandomUtil.getRandomEntry(WALLS_FALL);
        }
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
        EFFECT_SKILL.play(player);
    }
}
