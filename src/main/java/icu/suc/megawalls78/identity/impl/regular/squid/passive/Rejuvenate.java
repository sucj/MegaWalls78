package icu.suc.megawalls78.identity.impl.regular.squid.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rejuvenate extends CooldownPassive {

    private static final double HEALTH = 21.0D;

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 30, 4);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, 30, 0);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.TOTEM_OF_UNDYING, 8, 0);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });

    public Rejuvenate() {
        super("rejuvenate", 40000L);
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        if (COOLDOWN()) {
            Player player = PLAYER().getBukkitPlayer();
            if (condition(player)) {
                potion(player);
                EFFECT_SKILL.play(player);
                COOLDOWN_RESET();
            }
        }
    }

    private static boolean condition(Player player) {
        return player.getHealth() < HEALTH;
    }

    private void potion(Player player) {
        player.addPotionEffect(REGENERATION);
        player.addPotionEffect(RESISTANCE);
        summaryEffectSelf(player, REGENERATION, RESISTANCE);
    }
}
