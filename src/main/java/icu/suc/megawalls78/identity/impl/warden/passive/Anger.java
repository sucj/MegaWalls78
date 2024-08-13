package icu.suc.megawalls78.identity.impl.warden.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Anger extends Passive implements IActionbar {

    private static final int MIN = 20;
    private static final int DECREASE = 1;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 0);

    private boolean state;
    private int tick;

    public Anger() {
        super("anger");
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        if (state) {
            GamePlayer gamePlayer = PLAYER();
            Player player = gamePlayer.getBukkitPlayer();
            if (!player.isSprinting()) {
                deactivate(player);
            }
            if (tick % 20 == 0) {
                if (gamePlayer.getEnergy() > 0) {
                    gamePlayer.decreaseEnergy(DECREASE);
                    playSoundEffect(player);
                }
            }
            tick++;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnergyChange(EnergyChangeEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && event.getEnergy() == 0) {
            deactivate(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSprint(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && player.isSprinting() && !state) {
            activate(player);
        }
    }

    private void playSoundEffect(Player player) {
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private void speed(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.addPotionEffect(SPEED);
    }

    private void slow(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(SLOWNESS);
    }

    private void activate(Player player) {
        if (PLAYER().getEnergy() >= MIN) {
            state = true;
            tick = 0;
            speed(player);
        }
    }

    private void deactivate(Player player) {
        state = false;
        slow(player);
    }

    @Override
    public Component acb() {
        return Type.STATE.accept(state);
    }
}
