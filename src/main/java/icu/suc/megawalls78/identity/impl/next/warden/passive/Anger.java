package icu.suc.megawalls78.identity.impl.next.warden.passive;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Anger extends Passive implements IActionbar {

    private static final int MIN = 20;
    private static final int DECREASE = 1;

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0);
    private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 0);

    private int tick;
    private boolean state;
    private boolean deactivated;

    public Anger() {
        super("anger");
    }

    @EventHandler
    public void onPlayerTick(ServerTickStartEvent event) {
        GamePlayer gamePlayer = PLAYER();
        Player player = gamePlayer.getBukkitPlayer();
        if (player.isSprinting() && gamePlayer.getEnergy() > 0) {
            if (!state && gamePlayer.getEnergy() >= MIN) {
                activate(player);
            }
            if (state) {
                if (tick % 20 == 0) {
                    gamePlayer.decreaseEnergy(DECREASE);
                    playSoundEffect(player);
                }
                tick++;
            }
        } else if (!deactivated) {
            deactivate(player);
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
        state = true;
        deactivated = false;
        tick = 0;
        speed(player);
    }

    private void deactivate(Player player) {
        state = false;
        deactivated = true;
        slow(player);
    }

    @Override
    public Component acb() {
        return Type.STATE.accept(state);
    }

    @Override
    public void unregister() {
        tick = 0;
    }
}
