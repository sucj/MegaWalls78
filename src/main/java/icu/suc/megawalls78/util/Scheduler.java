package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {

    public static BukkitTask runTask(Runnable runnable) {
        return getScheduler().runTask(getPlugin(), runnable);
    }

    public static BukkitTask runTaskLater(Runnable runnable, long delay) {
        return getScheduler().runTaskLater(getPlugin(), runnable, delay);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return getScheduler().runTaskTimer(getPlugin(), runnable, delay, period);
    }

    public static BukkitTask runTaskAsync(Runnable runnable) {
        return getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    public static BukkitTask runTaskLaterAsync(Runnable runnable, long delay) {
        return getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
    }

    public static BukkitTask runTaskTimerAsync(Runnable runnable, long delay, long period) {
        return getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period);
    }

    private static BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }

    private static Plugin getPlugin() {
        return MegaWalls78.getInstance();
    }
}
