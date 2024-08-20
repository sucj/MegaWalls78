package icu.suc.mw78.identity.regular.arcanist.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Tempest extends Passive {

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);
    private static final PotionEffect SPEED_3 = new PotionEffect(PotionEffectType.SPEED, 120, 2);
    private static final double HEALTH = 1.0D;
    private static final PotionEffect SPEED_2 = new PotionEffect(PotionEffectType.SPEED, 60, 1);

    public Tempest() {
        super("tempest");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer gamePlayer = event.getPlayer();
        if (PASSIVE(gamePlayer)) {
            Player player = gamePlayer.getBukkitPlayer();
            player.addPotionEffect(REGENERATION);
            player.addPotionEffect(SPEED_3);
            summaryEffectSelf(player, REGENERATION, SPEED_3);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Assist event) {
        GamePlayer gamePlayer = event.getPlayer();
        if (PASSIVE(gamePlayer)) {
            Player player = gamePlayer.getBukkitPlayer();
            player.heal(HEALTH);
            summaryHealSelf(player);
            player.addPotionEffect(SPEED_2);
            summaryEffectSelf(player, SPEED_2);
        }
    }
}
