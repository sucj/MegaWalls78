package icu.suc.mw78.identity.todo.regular.blaze.passive;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public final class CallOfTheBlazes extends Passive {

    private final List<Blaze> blazes = Lists.newArrayList();

    public CallOfTheBlazes() {
        super("call_of_the_blazes");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer gamePlayer = event.getPlayer();
        if (PASSIVE(gamePlayer)) {
            clear();
            spawn(gamePlayer.getBukkitPlayer());
        }
    }

    private void spawn(Player player) {
        for (int i = 0; i < 2; i++) {
            EntityUtil.spawn(player.getLocation(), EntityUtil.Type.TEAM_BLAZE, entity -> {
                Blaze blaze = (Blaze) entity;
                blazes.add(blaze);
                blaze.customName(Component.translatable("mw78.entity.tamed", player.name(), blaze.name()));
                player.getScoreboard().getPlayerTeam(player).addEntity(entity);
            });
        }
    }

    private void clear() {
        for (Blaze blaze : blazes) {
            blaze.setHealth(0);
        }
        blazes.clear();
    }

    @Override
    public void unregister() {
        clear();
    }
}
