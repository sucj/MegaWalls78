package icu.suc.megawalls78.listener;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class EnergyListener implements Listener {

  private static final Map<Player, EnergyBlinker> BLINKERS = Maps.newHashMap();

  @EventHandler
  public void onEnergyChange(EnergyChangeEvent event) {
    Player player = event.getPlayer();
    int energy = event.getEnergy();
    Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> player.setLevel(energy));
    int max = event.getMax();

    if (energy == max) {
      if (!BLINKERS.containsKey(player)) {
        EnergyBlinker blinker = new EnergyBlinker(player);
        BLINKERS.put(player, blinker);
        blinker.setTask(Bukkit.getScheduler().runTaskTimerAsynchronously(MegaWalls78.getInstance(), blinker::run, 0L, 5L));
      }
    } else {
      Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> player.setExp((float) energy / max));
      EnergyBlinker blinker = BLINKERS.get(player);
      if (blinker != null) {
        blinker.cancel();
        BLINKERS.remove(player);
      }
    }
  }

  private static class EnergyBlinker extends BukkitRunnable {

    private final Player player;
    private boolean flag;

    private BukkitTask task;

    private EnergyBlinker(Player player) {
      this.player = player;
    }

    @Override
    public void run() {
      if (MegaWalls78.getInstance().getGameManager().isSpectator(player)) {
        cancel();
      } else {
        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
          player.setExp(flag ? 0.0F : 1.0F);
          flag = !flag;
        });
      }
    }

    public void setTask(BukkitTask task) {
      this.task = task;
    }

    public void cancel() {
      task.cancel();
      BLINKERS.remove(player);
    }
  }
}
