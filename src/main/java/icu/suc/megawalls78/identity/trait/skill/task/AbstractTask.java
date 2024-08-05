package icu.suc.megawalls78.identity.trait.skill.task;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractTask extends BukkitRunnable {

    protected final Player player;

    private final int deaths;

    public AbstractTask(Player player) {
        this.player = player;
        this.deaths = player.getStatistic(Statistic.DEATHS);
    }

    protected boolean shouldCancel() {
        return player.getStatistic(Statistic.DEATHS) > deaths;
    }
}
