package icu.suc.megawalls78.identity.impl.enderman.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SoulCharge extends Passive implements IActionbar {

    private static final long COOLDOWN = 15000L;
    private static final int ENERGY = 100;
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 200, 0);

    private long lastMills;

    public SoulCharge() {
        super("soul_charge");
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        GamePlayer gamePlayer = getPlayer();
        Player player = gamePlayer.getBukkitPlayer();
        long currentMillis = System.currentTimeMillis();
        if (shouldPassive(player) && currentMillis - lastMills >= COOLDOWN && gamePlayer.getEnergy() == ENERGY) {
            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, 1.0F, 1.0F);
            player.addPotionEffect(REGENERATION);
            lastMills = currentMillis;
        }
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acbValue() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }
}