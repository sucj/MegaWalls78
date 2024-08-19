package icu.suc.megawalls78.identity.impl.golem.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class IronHeart extends Passive {

    private static final PotionEffect ABSORPTION = new PotionEffect(PotionEffectType.ABSORPTION, 200, 1);

    public IronHeart() {
        super("iron_heart");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer player = event.getPlayer();
        if (PASSIVE(player)) {
            potion(player);
        }
    }

    public void potion(GamePlayer player) {
        Player bukkitPlayer = player.getBukkitPlayer();
        bukkitPlayer.addPotionEffect(ABSORPTION);
        summaryEffectSelf(bukkitPlayer, ABSORPTION);
    }
}
