package icu.suc.megawalls78.identity.impl.cow.passive;

import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.getIdentity;
import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class RefreshingSip extends Passive {

    private static final double RANGE = 7.0D;
    private static final double HEALTH = 4.0D;
    private static final int FOOD = 20;

    public RefreshingSip() {
        super("refreshing_sip");
    }

    @EventHandler
    public void onDrinkMilk(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (shouldPassive(player) && event.getItem().getType().equals(Material.MILK_BUCKET)) {

            AtomicInteger count = new AtomicInteger();
            player.heal(HEALTH);
            player.setFoodLevel(FOOD);
            PlayerUtil.setStarvation(player, FOOD);
            count.getAndIncrement();

            player.getNearbyEntities(RANGE, RANGE, RANGE).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> isValidAllies(player, entity))
                    .filter(entity -> !getIdentity((Player) entity).equals(Identity.COW))
                    .forEach(entity -> {
                        ((Player) entity).heal(HEALTH);
                        ((Player) entity).setFoodLevel(FOOD);
                        PlayerUtil.setStarvation(((Player) entity), FOOD);
                        count.getAndIncrement();
                    });
            summaryHeal(player, count.get());
        }
    }

    @Override
    public void unregister() {

    }
}
