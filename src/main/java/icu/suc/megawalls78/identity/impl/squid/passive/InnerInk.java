package icu.suc.megawalls78.identity.impl.squid.passive;

import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class InnerInk extends Passive {

    private static final double RANGE = 5.0D;
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 60, 0);

    public InnerInk() {
        super("inner_ink");
    }

    @EventHandler
    public void onDrinkPotion(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        AtomicInteger count = new AtomicInteger();
        if (shouldPassive(player) && event.getItem().getType().equals(Material.POTION)) {
            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            EntityUtil.getNearbyEntities(player, RANGE).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        ((Player) entity).addPotionEffect(BLINDNESS);
                        count.getAndIncrement();
                    });
            summaryHit(player, count.get());
        }
    }

    @Override
    public void unregister() {

    }
}
