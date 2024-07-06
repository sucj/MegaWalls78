package icu.suc.megawalls78.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EnergyChangeEvent extends Event implements Cancellable {

  private final Player player;
  private final int origin;
  private final int energy;
  private final int max;

  private boolean cancelled;

  private static final HandlerList handlers = new HandlerList();

  public EnergyChangeEvent(Player player, int origin, int energy, int max) {
    this.player = player;
    this.origin = origin;
    this.energy = energy;
    this.max = max;
  }

  public Player getPlayer() {
    return player;
  }

  public int getOrigin() {
    return origin;
  }

  public int getEnergy() {
    return energy;
  }

  public int getMax() {
    return max;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean b) {
    cancelled = b;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
