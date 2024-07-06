package icu.suc.megawalls78.game;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpectatorRunner extends BukkitRunnable {

    private BukkitTask task;

    private final Player player;
    private long timer;

    public SpectatorRunner(Player player) {
        this.player = player;
        this.timer = MegaWalls78.getInstance().getConfigManager().respawnTime;
        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Component component = Component.translatable("mw78.respawn", NamedTextColor.AQUA, Component.translatable("mw78.seconds", MessageUtil.second(timer)));
            if (timer > 10000L || timer < 10000L && timer > 5000L) {
                MessageUtil.sendMessage(component, player);
            }
            MessageUtil.sendTitle(Component.translatable("mw78.died", NamedTextColor.RED), component, MessageUtil.ONE_SEC_TIMES, player);
        });
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.task.cancel();
        } else if (timer <= 0) {
            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                player.setHealth(0);
                MegaWalls78.getInstance().getGameManager().removeSpectator(player);
                MegaWalls78.getInstance().getSkinManager().applySkin(player);
            });
            this.task.cancel();
        } else {
            if (timer == 10000L || timer <= 5000L) {
                Component component = Component.translatable("mw78.respawn", NamedTextColor.AQUA, Component.translatable("mw78.seconds", MessageUtil.second(timer)));
                Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                    MessageUtil.sendMessage(component, player);
                    MessageUtil.sendTitle(Component.translatable("mw78.died", NamedTextColor.RED), component, MessageUtil.ONE_SEC_TIMES, player);
                });
            }
            timer -= 1000L;
        }
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
